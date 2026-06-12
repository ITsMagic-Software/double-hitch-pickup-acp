/* @Author ITsMagic */ 

public float startPitch = 0.8f;
public float pitchRange = 0.4f;
public int numberOfGears = 5;
public float gearPitchOffset = 0.1f;
public float gearLengthPow = 2f;

@AutoWired
private SoundPlayer sp;

/// Run only once
void start() {
    
}

/// Repeat every frame
void repeat() {
    ICarMotor cm = (ICarMotor) myObject.findComponentInParent("CarMotor");
    float s = cm.getSpeedPercentage();
    s = 1f - s;
    s = Math.pow(s, gearLengthPow);
    s = 1f - s;
    if(Float.isNaN(s)) s = cm.getSpeedPercentage();
    
    float gearRange = 1f / (float)numberOfGears;
    float currentGearF = s / gearRange;
    
    int curGear = ((int)currentGearF);
    float gearProgress = currentGearF - curGear;
    
    float p = startPitch + (gearProgress * pitchRange) + (curGear * gearPitchOffset);
    //print("Pitch:"+ p + "\nGear:"+(curGear+1)+"\nSP:"+cm.getSpeedPercentage());
    sp.pitch = p;
}