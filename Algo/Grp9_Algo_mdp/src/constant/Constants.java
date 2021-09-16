package constant;

/**
 * @author Nicholas Yeo Ming Jie
 * @author Neo Zhao Wei
 * @author David Loh Shun Hao
 * @version 1.0
 * @since 2020-10-27
 */
public class Constants {

    public static final int MAX_ROW = 20;
    public static final int MAX_COL = 20;
    public static final int MAX_VEl = 60;

    public static final int WITHIN_3BY3[] = {-1, 0, 1};
    public static final int LONG_SENSOR_ADD[] = {2, 3, 4, 5};
    public static final int LONG_SENSOR_SUB[] = {-2, -3, -4, -5};
    public static final int SHORT_SENSOR_ADD[] = {2, 3};
    public static final int SHORT_SENSOR_SUB[] = {-2, -3};

    public static final int TURN_COST = 60;
    public static final int MOVE_COST = 10;
    public static final int FORWARD_COST = 10; //move
    public static final int REVERSE_COST = 10; //turn twice and move
    public static final int RIGHT_LEFT_COST = 70; //turn once and move
    public static final int INFINITE_COST = 9999;

    public static final int OBSTACLE_IMMEDIATE = 1;
    public static final int OBSTACLE_ONE_BLOCK_AWAY = 2;
    public static final int NO_OBSTACLE = 3;

    public enum DIRECTION {
        NORTH, EAST, SOUTH, WEST;
    }

    public enum MOVEMENT {
        FORWARD, BACKWARD, RIGHT, LEFT;
    }

}
