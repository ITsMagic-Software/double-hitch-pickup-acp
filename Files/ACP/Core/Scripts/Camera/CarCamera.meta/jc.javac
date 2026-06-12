/* @Author ITsMagic */

@AutoWired
private SmoothCameraFollow scf;
@AutoWired
private OrbitalCameraFollow ocf;

/// Run only once
void start() {}

/// Repeat every frame
void repeat() {
  SpatialObject target = findTarget();
  if (target != null) {
    if (!target.enabled) {
      target = null;
    } else {
      CarMotor cm = target.findComponent(CarMotor.class);
      if (cm == null || !cm.engineOn) {
        target = null;
      }
    }
  }

  // find a new car to attach to smooth camera follower
  if (target == null) {
    List<Component> cms = WorldController.listAllComponents(CarMotor.class);
    for (int x = 0; x < cms.size(); x++) {
      CarMotor cm = (CarMotor) cms.get(x);
      if (cm.engineOn) {
        target = cm.myObject;
        CarCameraSettings ccs = target.findComponent(CarCameraSettings.class);
        if (ccs != null) {
          if (scf != null && scf.enabled) {
            scf.distance = ccs.distance;
            scf.height = ccs.height;
          } else if (ocf != null && ocf.enabled) {
            ocf.distance = ccs.distance;
            ocf.height = ccs.height;
          } 
        }
        setTarget(target);
        break;
      }
    }
  }
}

SpatialObject findTarget() {
  if (scf != null && scf.enabled) {
    return scf.target;
  } else if (ocf != null && ocf.enabled) {
    return ocf.target;
  }
  return null;
}

void setTarget(SpatialObject o) {
  if (scf != null && scf.enabled) {
    scf.target = o;
  } else if (ocf != null && ocf.enabled) {
    ocf.target = o;
  }
}