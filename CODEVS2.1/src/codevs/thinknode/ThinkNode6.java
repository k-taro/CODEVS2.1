package codevs.thinknode;

import java.util.ArrayList;

import codevs.field.Field;
import codevs.main.CodeVs;

public class ThinkNode6 extends ThinkNode2 {

	public ThinkNode6(int turn, Field field, long score, int fcp1, int x, int r) {
		super(turn, field, score, fcp1, x, r);
	}
	
	@Override
	public long setChild(){
		ThinkNode6 max_child = new ThinkNode6(0,new Field(),0,1, 0, 0);
		ArrayList<ThinkNode6> temp_list = new ArrayList<ThinkNode6>();
		
		for(int x = 1 - CodeVs.Pack_size; x<CodeVs.Field_wid; x++){
			for(int r = 0; r<4; r++){
				Field temp = field.clone();
				long raw_score = temp.putPack(CodeVs.pack[this.turn+1].getRotatedPack(r), x);
				if(raw_score > -1){
					ThinkNode6 node;
					long score = raw_score * Fcp1;
					if(raw_score >= CodeVs.Th){
						node = new ThinkNode6(this.turn+1, temp, score, Fcp1 + 1, x, r);
					}else{
						node = new ThinkNode6(this.turn+1, temp, score, Fcp1, x, r);
					}
					
					if(2*(this.brock_num - node.brock_num) > Math.pow(this.brock_num, 2)/(CodeVs.Field_wid * CodeVs.hei_p_size)){// 消えたブロック数が多いものはゴールとして、移行の手を見ない
						if(max_child.score < node.score){
							max_child = node;
						}
					}else{
						temp_list.add(node);
					}
					
				}
			}
		}
		
		if(max_child.score > 0){
			child.add(max_child);
		}
		
		for(int i = 0; i<15; i++){
			int index = temp_list.size();
			if(index == 0){
				break;
			}
			while(index == temp_list.size()){
				index = (int) (Math.random() * index);
			}
			child.add(temp_list.get(index));
			temp_list.remove(index);
		}
		
		return max_child.score;
	}
	
	@Override
	public long getMaxScore(){
		long max_score = 0;
		int turn_dif = this.turn - CodeVs.Turn;
		if(this.turn < CodeVs.TURN_NUM-1 && turn_dif < 4){				// 読む手数を変えるにはここを変える。setUP()も。
			if(child.size() == 0){
				setChild();
				
				if(child.size() == 0){
					return score;
				}
			}
			
			ThinkNode6 node = (ThinkNode6) child.get(0);
			if(2*(this.brock_num - node.brock_num) > Math.pow(this.brock_num, 2)/(CodeVs.Field_wid * CodeVs.hei_p_size)){// 消えたブロック数が多いものはゴールとして、移行の手を見ない
				if(max_score < node.score){
					max_score = node.score;
				}
			}else{
				long temp_score = node.getMaxScore();
				if(max_score < temp_score){
					max_score = temp_score;
				}				
			}
			
			for(int index = 1; index<child.size(); index++){
				node = (ThinkNode6) child.get(index);
				long temp_score = node.getMaxScore();
				if(max_score < temp_score){
					max_score = temp_score;
				}
			}
		}
		return score + max_score;
	}

}
