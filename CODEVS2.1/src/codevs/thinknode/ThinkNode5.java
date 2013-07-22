package codevs.thinknode;

import codevs.field.Field;
import codevs.main.CodeVs;

public class ThinkNode5 extends ThinkNode2 {

	public ThinkNode5(int i, Field temp, long score, int fcp1, int x, int r) {
		super(i, temp, score, fcp1, x, r);
	}

	@Override
	public long setChild(){
		ThinkNode5 max_child = new ThinkNode5(0,new Field(),0,1, 0, 0);
		
		for(int x = 1 - CodeVs.Pack_size; x<CodeVs.Field_wid; x++){
			for(int r = 0; r<4; r++){
				Field temp = field.clone();
				long raw_score = temp.putPack(CodeVs.pack[this.turn+1].getRotatedPack(r), x);
				if(raw_score > -1){
					ThinkNode5 node;
					long score = raw_score * Fcp1;
					if(raw_score >= CodeVs.Th){
						node = new ThinkNode5(this.turn+1, temp, score, Fcp1 + 1, x, r);
					}else{
						node = new ThinkNode5(this.turn+1, temp, score, Fcp1, x, r);
					}
					child.add(node);
					if(max_child.score < score){
						max_child = node;
					}
				}
			}
		}
		
		// 枝切りのパラメータ
//		if(brock_num < 100){
//			int size = child.size();
//			for(int index = 0; index < size*(100-brock_num)/250.0; index ++){
//				Child temp_c = child.get((int)(Math.random() * (child.size() - 0.01)));
//				if(temp_c != max_child){
//					child.remove(temp_c);
//				}
//			}
//		}

		return max_child.score;
	}
	
	@Override
	public long getMaxScore(){
		long max_score = 0;
		int turn_dif = this.turn - CodeVs.Turn;
		if(this.turn < CodeVs.TURN_NUM-1 && turn_dif < 3){				// 読む手数を変えるにはここを変える。setUP()も。
			if(child.size() == 0){
//				if(turn_dif > 1){ // ゴールにまっしぐらで後々自滅するのを防ぐ
//					if(this.score> Math.pow((brock_num-10)/10,2)){// 点数がある程度高いものはゴールとして、移行の手を見ない
//						return 0;
//					}
//				}
				setChild();
				for(int index = 0; index<child.size(); index++){
					ThinkNode2 node = (ThinkNode5) child.get(index);
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
				}
			}else{
				for(int index = 0; index<child.size(); index++){
					ThinkNode2 node = (ThinkNode5) child.get(index);
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
				}
			}
		}
		return score + max_score;
	}
}
