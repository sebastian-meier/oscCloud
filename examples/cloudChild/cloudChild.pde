import oscP5.*;
import netP5.*;

import circus.*;

OscCloud cloud;

void setup(){
  size(200, 200);
  cloud = new OscCloud(this, "child", "10.254.255.205", 1338, "10.254.255.205", 1337);
}

void draw(){
  background(255);
  fill(0);
  if(cloud.registered){
    text(cloud.currentTime, 10, 20);
  }
}

void oscEvent(OscMessage theOscMessage) {
  cloud.oscEvent(theOscMessage);
}


