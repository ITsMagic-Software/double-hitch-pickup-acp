/** @Author ITsMagic */
public VehicleWheel pivotWheel;

public VehicleWheel lookWheel;

/// Repeat every frame
@Override
public void repeat() {
  execute();
}

@Override
void stoppedRepeat() {
  execute();
}

void execute() {
  if (pivotWheel != null && pivotWheel.myObject != null) {
    if (lookWheel != null && lookWheel.myObject != null) {
      Vector3 wantedPos = findObject(pivotWheel).getGlobalPosition();
      SpatialObject parent = myObject.parent;
      wantedPos = parent.inverseTransformPoint(wantedPos);
      myObject.position = wantedPos;

      Vector3 lookPos = findObject(lookWheel).getGlobalPosition();
      myObject.lookTo(lookPos);
    }
  }
}

SpatialObject findObject(VehicleWheel vw) {
  return vw.myObject.getChildAt(0);
}