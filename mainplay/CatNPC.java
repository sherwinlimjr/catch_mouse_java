package mainplay;

public class CatNPC {
    public enum State { PATROL, CHASE, RETREAT }
    
    public int row, col;
    public int startRow, startCol;
    public State state = State.PATROL;
    
    private int patrolStartCol;
    private int patrolEndCol;
    private int patrolDir = 1;
    
    public CatNPC(int r, int c, int pStart, int pEnd) {
        this.row = r;
        this.col = c;
        this.startRow = r;
        this.startCol = c;
        this.patrolStartCol = pStart;
        this.patrolEndCol = pEnd;
    }
    
    public int direction = 0; // 0=Right, 1=Left, 2=Up, 3=Down

    public void update(int mr, int mc, int[][] maze) {
        int cr = row;
        int cc = col;
        
        double dist = Math.sqrt(Math.pow(mr - cr, 2) + Math.pow(mc - cc, 2));
        
        if (state == State.PATROL) {
            boolean canSee = (mr == cr) && ((patrolDir == 1 && mc > cc && mc <= cc + 5) || (patrolDir == -1 && mc < cc && mc >= cc - 5));
            if (canSee) {
                state = State.CHASE;
                updateDir(0, mr > cr ? 1 : (mr < cr ? -1 : 0)); // Initial chase direction
            } else {
                if (cc >= patrolEndCol) patrolDir = -1;
                else if (cc <= patrolStartCol) patrolDir = 1;
                
                if (!GameConfig.isWall(maze, cr, cc + patrolDir)) {
                    col += patrolDir;
                    updateDir(0, patrolDir);
                } else {
                    patrolDir *= -1;
                    updateDir(0, patrolDir);
                }
            }
        } else if (state == State.CHASE) {
            if (dist > 8) state = State.RETREAT;
            else {
                if (mr < cr) move(-1, 0, maze);
                else if (mr > cr) move(1, 0, maze);
                else if (mc < cc) move(0, -1, maze);
                else if (mc > cc) move(0, 1, maze);
            }
        } else if (state == State.RETREAT) {
            if (row == startRow && col == startCol) state = State.PATROL;
            else {
                if (startRow < cr) move(-1, 0, maze);
                else if (startRow > cr) move(1, 0, maze);
                else if (startCol < cc) move(0, -1, maze);
                else if (startCol > cc) move(0, 1, maze);
            }
        }
    }
    
    private void move(int dr, int dc, int[][] maze) {
        if (!GameConfig.isWall(maze, row + dr, col + dc)) {
            row += dr;
            col += dc;
            updateDir(dr, dc);
        }
    }

    private void updateDir(int dr, int dc) {
        if (dc == 1) direction = 0;
        else if (dc == -1) direction = 1;
        else if (dr == -1) direction = 2;
        else if (dr == 1) direction = 3;
    }
}
