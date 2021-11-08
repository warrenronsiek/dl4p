DL4P
====
I found that setting up a local MacOS dev environment for neanderthal was excessively complicated. MacOS has been 
idiot-proofed to the extent that it isn't poweruser friendly anymore. The goal of this project is to provide utilities 
that do an end-run around this problem. Specifically, it helps you in setting up remote development environments on EC2 
and using nREPL to allow for a local clojure development experience on arbitrarily powerful clojure/neanderthal boxes
(on the cheap!).

Setup/Requirements
------------------
* AWS keys with appropriate permissions stored in env vars on local machine
* Local uses java11, but java8 will probably work too.

Usage
-----
First, `lein uberjar` to build the cli.

### creating an instance
`java -jar ./target/dl4p.jar create-instance --conf my_conf.yml`

where `my_conf.yml` has the following format (subbing in your own values):

```yaml
zone: us-west-2
instance-type: c5.large
pem-key: mykey 
security-group: sg-133456789
subnet-id: 
```

you can then access the remote repl by 
1. run `ssh -N -L 40000:127.0.0.1:40000 ubuntu@<your-ip> -i ~/path/to/<key-name>.pem`
2. run an nREPL against port 40000 e.g.:
![nrepl-config](./nrepl-config.png)