package codevs.thinknode;

import java.util.ArrayList;
import java.util.HashSet;

import codevs.field.Field;
import codevs.main.CodeVs;

public class ThinkNode_l_ScoreUP extends ThinkNode2 {
	protected boolean haveSetChild;

	public ThinkNode_l_ScoreUP(int turn, Field field, long score, int fcp1, int x, int r) {
		super(turn, field, score, fcp1, x, r);
		// TODO 自動生成されたコンストラクター・スタブ
		haveSetChild = false;
	}
	
	@Override
	public long setChild(){
		ThinkNode_l_ScoreUP max_child = new ThinkNode_l_ScoreUP(0,new Field(),0,1, 0, 0);
		ArrayList<ThinkNode_l_ScoreUP> temp_list = new ArrayList<ThinkNode_l_ScoreUP>();
		
		for(int x = 1 - CodeVs.Pack_size; x<CodeVs.Field_wid; x++){
			for(int r = 0; r<4; r++){
				Field temp = field.clone();
				long raw_score = temp.putPack(CodeVs.pack[this.turn+1].getRotatedPack(r), x);
				if(raw_score > -1){
					ThinkNode_l_ScoreUP node;
					long score = raw_score * Fcp1;
					if(raw_score >= CodeVs.Th){
						node = new ThinkNode_l_ScoreUP(this.turn+1, temp, score, Fcp1 + 1, x, r);
					}else{
						node = new ThinkNode_l_ScoreUP(this.turn+1, temp, score, Fcp1, x, r);
					}
					
					if(3*(this.brock_num - node.brock_num) > Math.pow(this.brock_num, 2)/(CodeVs.Field_wid * CodeVs.hei_p_size)){// 消えたブロック数が多いものはゴールとして、移行の手を見ない
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
		
		HashSet<Integer> set = new HashSet<Integer>();
		
		int limit = 21;
		if(temp_list.size()<21){
			child.addAll(temp_list);
		}else{

			while(set.size() < limit){
				int index;
				do{
					index = (int) (Math.random() * temp_list.size());				
				}while(index == temp_list.size());
				set.add(new Integer(index));
			}

//			Integer[] list = (Integer[]) set.toArray();
			Integer[] list = new Integer[21]; 
			set.toArray(list);

			for(int i = 0; i<list.length; i++){
				child.add(temp_list.get(list[i].intValue()));
			}
		}

		return max_child.score;
	}
	
	@Override
	public long getMaxScore(){
		long max_score = 0;
		int turn_dif = this.turn - CodeVs.Turn;
		if(this.turn < CodeVs.TURN_NUM-1 && turn_dif < 3){				// 読む手数を変えるにはここを変える。setUP()も。
			if(child.size() == 0){
				if(!haveSetChild){
					setChild();
				}
				
				if(child.size() == 0){
					return score;
				}
			}
			
			ThinkNode_l_ScoreUP node = (ThinkNode_l_ScoreUP) child.get(0);
			if(3*(this.brock_num - node.brock_num) > Math.pow(this.brock_num, 2)/(CodeVs.Field_wid * CodeVs.hei_p_size)){// 消えたブロック数が多いものはゴールとして、移行の手を見ない
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
				node = (ThinkNode_l_ScoreUP) child.get(index);
				long temp_score = node.getMaxScore();
				if(max_score < temp_score){
					max_score = temp_score;
				}
			}
		}
		return score + max_score;
	}


}
