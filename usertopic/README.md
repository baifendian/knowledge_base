# user topic subscribe topology

## function

user action for (un)subscribing topics 
 
## data stream
kafka -> storm -> codis

## user action

key : gid
value : topics
action : add or del topic 

## java class func

UserTopicTopo : main class for submitting topology
WriteCodisBolt : core logic
PrintBolt : for test
RandomSpout : for test