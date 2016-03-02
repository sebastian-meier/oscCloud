import oscP5.*;
import netP5.*;

import circus.*;

OscCloud cloud;

void setup(){
  size(200, 200);
  cloud = new OscCloud(this, "parent", "10.254.255.205", 1337);
}

void draw(){
  background(255);
  fill(0);
  text(cloud.time, 10, 20);
}

void mousePressed(){
  cloud.running = true;
}

void oscEvent(OscMessage theOscMessage) {
  cloud.oscEvent(theOscMessage);
}


