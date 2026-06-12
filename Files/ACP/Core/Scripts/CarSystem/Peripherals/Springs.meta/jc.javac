/** @Author ITsMagic */
public SpatialObject axle;

public float size = 1f;
public boolean look = false;

@Override
public void repeat() {
  execute();
}

@Override
void stoppedRepeat() {
    execute();
}

void execute() {
  if (axle != null && axle.exists()) {
    float dist = myObject.distance(axle);
    myObject.scale.z = dist * size;

    if (look) {
      myObject.lookTo(axle);
    }
    myObject.transform.recalculateMatrices();
  } 
}