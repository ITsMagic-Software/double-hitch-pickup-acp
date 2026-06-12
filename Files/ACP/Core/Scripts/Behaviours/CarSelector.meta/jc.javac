/* @Author ITsMagic */

private List<CarMotor> cars = new ArrayList();

private UITextView carNameDisplayer;

/// Run only once
void start() {
  List<Component> cms = WorldController.listAllComponents(CarMotor.class);
  for (int x = 0; x < cms.size(); x++) {
    CarMotor cm = (CarMotor) cms.get(x);
    cars.add(cm);
  }

  carNameDisplayer = myObject.getChildAt(1).findComponent(UITextView.class);
  int ac = determineActiveCarID();
  if (ac < 0) {
    activateCar(0);
  } else {
    activateCar(ac);
    carNameDisplayer.setText(cars.get(ac).myObject.name);
  }
} 

/// Repeat every frame
void repeat() {
  if (Input.isKeyDown("previous_car")) {
    int ac = determineActiveCarID();
    if (ac < 0) {
      ac = 0;
    } else {
      if (ac >= 1) {
        ac--;
      } else {
        ac = cars.size() - 1;
      }
    }
    activateCar(ac);
  }

  if (Input.isKeyDown("next_car")) {
    int ac = determineActiveCarID();
    if (ac < 0) {
      ac = 0;
    } else {
      if (ac < cars.size() - 1) {
        ac++;
      } else {
        ac = 0;
      }
    }
    activateCar(ac);
  }
}

void activateCar(int id) {
  for (int x = 0; x < cars.size(); x++) {
    CarMotor cm = cars.get(x);
    if (x == id) {
      cm.engineOn = true;
      carNameDisplayer.setText(cm.myObject.name);
    } else {
      cm.engineOn = false;
    }
  }
}

int determineActiveCarID() {
  for (int x = 0; x < cars.size(); x++) {
    CarMotor cm = cars.get(x);
    if (cm.engineOn) {
      return x;
    }
  }
  return -1;
}