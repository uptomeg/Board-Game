package assignment1;

import java.util.Random;

public class GrowthingAgent extends Agent{
	private int gameCount = 0;
	private State state;
	
	public GrowthingAgent(Board board, int count) {
        super(board);
        this.gameCount = count;
        state = new RandomAgent(board);
    }
	
	@Override
	public Move getMove() {
		// TODO Auto-generated method stub
		Random r = new Random();
		if (gameCount <= 2) {
			return state.getMove();
		}
		
		else if( gameCount <= 10){
			int num = r.nextInt(2);
			//System.out.print("\n" + "random number is :" + num + "\n");
			if(num == 0) {
				state = new GreedyAgent(board);
				return state.getMove(); 
			}
			return state.getMove();
		}else {
			int num = r.nextInt(5);
			if(num == 0) {
				return state.getMove();
			}
			state = new GreedyAgent(board);
			return state.getMove(); 
		}
	}
	

}
