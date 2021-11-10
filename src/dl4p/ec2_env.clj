(ns dl4p.ec2-env
  (:require [cognitect.aws.client.api :as aws]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as s]
            [taoensso.timbre :as t]
            [cljs.core.async :refer [go timeout alts! take! put! chan <! >! close!]])
  (:import java.util.Base64))

(def ec2-client (aws/client {:api :ec2 :region "us-west-2"}))

(def userdata
  (.encodeToString
    (Base64/getEncoder)
    (.getBytes (s/join "\n"
                       ["#!/bin/bash"
                        "sudo apt update"
                        "sudo apt -y install cmake pkg-config build-essential openjdk-8-jre-headless"
                        "curl -O https://download.clojure.org/install/linux-install-1.10.3.1020.sh"
                        "chmod +x linux-install-1.10.3.1020.sh"
                        "sudo ./linux-install-1.10.3.1020.sh"
                        "sudo apt -y install leiningen"
                        "ssh-keyscan -t rsa github.com >> ~/.ssh/known_hosts"
                        "git clone https://github.com/warrenronsiek/dl4p.git"
                        "cd dl4p"
                        "lein deps"
                        "lein trampoline repl :headless :start :port 40000 &"]))))

(defn create-instance [{:keys [zone instance-type pem-key security-group subnet-id]}]
  (let [key-name (first (s/split (last (s/split pem-key #"/")) #"\."))
        spot-request (t/spy :info
                            (aws/invoke ec2-client
                                        {:op      :RequestSpotInstances
                                         :request {:AvailabilityZoneGroup zone
                                                   :LaunchSpecification   {:ImageId             "ami-09889d8d54f9e0a0e"
                                                                           :UserData            userdata
                                                                           :InstanceType        instance-type
                                                                           :EbsOptimized        false
                                                                           :KeyName             key-name

                                                                           :NetworkInterfaces   [{:DeleteOnTermination      true
                                                                                                  :Groups                   [security-group]
                                                                                                  :SubnetId                 subnet-id
                                                                                                  :AssociatePublicIpAddress true
                                                                                                  :DeviceIndex              0}]
                                                                           :BlockDeviceMappings [{:DeviceName "/dev/sda1"
                                                                                                  :Ebs        {:VolumeSize          300
                                                                                                               :DeleteOnTermination true
                                                                                                               :VolumeType          "gp2"
                                                                                                               :Encrypted           false}}]}
                                                   :Type                  "one-time"
                                                   :SpotPrice             0.033
                                                   :InstanceCount         1}}))
        spot-request-id (t/spy :info (:SpotInstanceRequestId (first (:SpotInstanceRequests spot-request))))
        _ (Thread/sleep 5000)
        spot-requests (t/spy :info (aws/invoke ec2-client {:op      :DescribeSpotInstanceRequests
                                                           :request {:SpotInstanceRequestIds [spot-request-id]}}))
        instance-id (t/spy :info (:InstanceId (first (:SpotInstanceRequests spot-requests))))
        instances (aws/invoke ec2-client {:op      :DescribeInstances
                                          :request {:InstanceIds [instance-id]}})
        public-ip (t/spy :info (:PublicIpAddress (first (:Instances (first (:Reservations instances))))))]
    (future (sh (str "ssh -N -L 40000:127.0.0.1:40000 ubuntu@" public-ip " -i" pem-key)))))