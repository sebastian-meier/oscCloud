# oscCloud
Java Framework to help synchronize multiple computers via osc in a local network

## Important Note:
This is a project i did in 2010, it was one of my first java projects and i haven't touched it since, this repo is for documentation purpose only. The libraries included in this repo are from back then and nothing has been tested with recent versions of processing or java or anything else.

## Info
The oscCloud tool was written to synchronize multiple computers in a local network. We used it to synchronize multiple projections, which were running on multiple computers spread across a room. You can also use the system to send messages between machines. The idea is to have a master (parent) system, that sends a timer ping to all the connected children in the network. If a child sends a message to the parent, the parent distributes the message to the other children.
