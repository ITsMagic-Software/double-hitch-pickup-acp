/* @Author ITsMagic */ 

public float timeOut = 3;
private float timer;

void repeat() {
    timer += Math.bySecond();
    if(timer >= timeOut){
        myObject.destroy();
        timer = 0;
    }
}
