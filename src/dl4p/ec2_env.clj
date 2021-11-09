(ns dl4p.ec2-env
  (:require [cognitect.aws.client.api :as aws]
            [clojure.string :as s])
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
  (let [dl4p-id (str "dl4p-" (rand-int 1000))]
    (aws/invoke ec2-client {:op      :RequestSpotInstances
                            :request {:AvailabilityZoneGroup zone
                                      :LaunchSpecification   {:ImageId             "ami-09889d8d54f9e0a0e"
                                                              :UserData            userdata
                                                              :InstanceType        instance-type
                                                              :EbsOptimized        false
                                                              :KeyName             pem-key
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
                                      :Tags                  [{:Key "name" :Value "dl4p-testing"}
                                                              {:Key "dl4p-id" :Value dl4p-id}]
                                      :SpotPrice             0.033
                                      :InstanceCount         1}})))