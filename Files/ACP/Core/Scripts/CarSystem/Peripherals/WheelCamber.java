/** @Author ITsMagic */
@AutoWired 
private VehicleWheel vw;

public float restCamber = 0;
public float upCamber = 20;
public float downCamber = 10;
public float upDeadZone = 0.3f;
public float downDeadZone = 0f;

private boolean invertSide;
/// Run only once
@Override
public void start() {
  invertSide = determineInverseSide();
}

/// Repeat every frame
@Override
public void posWheelPhysics() { 
  if(vw == null) return;
  float traveledPercentage = vw.getSuspensionTravelPercentage();
  float restLength = vw.getSuspensionRestLength();

  float applyCamber = 0;
  if (traveledPercentage >= restLength - upDeadZone && traveledPercentage <= restLength + downDeadZone) {
    applyCamber = restCamber;
  } else if (traveledPercentage < restLength - upDeadZone) {
    float rll = restLength - upDeadZone;
    float range = traveledPercentage / rll;
    range = 1f - range;
    applyCamber = -range * upCamber;
  } else if (traveledPercentage > restLength + downDeadZone) {
    float rll = restLength + downDeadZone;
    float range = (traveledPercentage - rll) / (1f - rll);
    applyCamber = range * downCamber;
  }

  if (invertSide) {
    applyCamber *= -1f;
  }

  vw.camber = applyCamber;
}

/* TODO: reduce ram usage */
public boolean determineInverseSide() {
  Matrix4 parentMatrix = new Matrix4();
  parentMatrix.set(myObject.mainParent.getGlobalMatrix());
  parentMatrix = parentMatrix.inverse();

  Matrix4 myMatrix = new Matrix4();
  myMatrix.set(myObject.getGlobalMatrix());
  myMatrix = parentMatrix.mul(myMatrix);

  if (myMatrix.getTranslation().getX() >= 0) {
    return true;
  }
  return false;
}