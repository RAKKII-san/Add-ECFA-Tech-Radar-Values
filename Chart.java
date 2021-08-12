public class Chart {
    private int position;
    private String difficulty;
    private int meter;
    private int speed;
    private int stamina;
    private int tech;
    private int movement;
    private int timing;
    private String gimmick;
    private boolean hasStyle;

    public Chart() {
        meter = 0;
        speed = 0;
        stamina = 0;
        tech = 0;
        movement = 0;
        timing = 0;
        gimmick = "none";
        hasStyle = false;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int newPos) {
        position = newPos;
    }

    public boolean getStyle() {
        return hasStyle;
    }

    public void setStyle(boolean style) {
        hasStyle = style;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String newDiff) {
        difficulty = newDiff;
    }

    public int getMeter() {
        return meter;
    }

    public void setMeter(int newMeter) {
        meter = newMeter;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int newSpd) {
        speed = newSpd;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int newStam) {
        stamina = newStam;
    }

    public int getTechnique() {
        return tech;
    }

    public void setTechnique(int newTech) {
        tech = newTech;
    }

    public int getMovement() {
        return movement;
    }

    public void setMovement(int newMvmt) {
        movement = newMvmt;
    }

    public int getRhythms() {
        return timing;
    }

    public void setRhythms(int newTmng) {
        timing = newTmng;
    }

    public String getGimmicks() {
        return gimmick;
    }

    public void setGimmicks(String newGmck) {
        gimmick = newGmck;
    }

    public String toString() {
        return "speed=" + speed + ",stamina=" + stamina + ",tech=" + tech +
        ",movement=" + movement + ",timing=" + timing + ",gimmick=" + gimmick;
    }
}
