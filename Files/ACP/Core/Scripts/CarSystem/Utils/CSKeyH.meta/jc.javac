/** @Author ITsMagic */
public class CSKeyH {
  public String name;

  private Key k;

  public CSKeyH() {
      
  } 

  public CSKeyH(String name) {
    this.name = name;
  }

  public Key get() {
    if (k == null || !k.getName().equals(name)) {
      k = Input.getKey(name);
    }
    return k;
  }
 
 public boolean pressed(){
     return get().pressed;
 }
}