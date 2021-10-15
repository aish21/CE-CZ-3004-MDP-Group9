package constant;
/**
 * @author Goh Cheng Guan, Clive
 * @author Lau Zhen Jie
 * @version 1.0
 * @since 2021-9-15
 */
public class Constants {

    public static final int MAX_ROW = 20;
    public static final int MAX_COL = 20;
    public static final int MAX_VEl = 60;

    public static final int WITHIN_3BY3[] = {-1, 0, 1};
    
    public static final int MOVE_COST = 10;
    public static final int RIGHT_LEFT_COST = 70; 


    public enum DIRECTION {
        NORTH, EAST, SOUTH, WEST;
    }

    public enum MOVEMENT {
        FORWARD, BACKWARD, RIGHT, LEFT;
    }

}
