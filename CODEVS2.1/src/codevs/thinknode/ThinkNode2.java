package codevs.thinknode;

import java.io.IOException;

import codevs.field.Field;
import codevs.main.CodeVs;

public class ThinkNode2 extends ThinkNode {
	
	protected int brock_num;

	public ThinkNode2(int turn, Field field, long score, int fcp1, int x, int r) {
		super(turn, field, score, fcp1, x, r);
		brock_num = field.getBrockNum();
	}
	
	@Override
	public void setUP(){
	}
	
	@Override
	public long getMaxScore(){
		long max_score = 0;
		int turn_dif = this.turn - CodeVs.Turn;
		if(this.turn < CodeVs.TURN_NUM-1 && turn_dif < 3){				// 読む手数を変えるにはここを変える。setUP()も。
			if(child.size() == 0){
//				if(turn_dif > 1){ // ゴールにまっしぐらで後々自滅するのを防ぐ
//					if(this.score> Math.pow((brock_num-10)/10,2)){
//						return 0;
//					}
//				}
				setChild();
				for(int index = 0; index<child.size(); index++){
					ThinkNode2 node = (ThinkNode2) child.get(index);
					if(2*(this.brock_num - node.brock_num) > Math.pow(this.brock_num, 2)/(CodeVs.Field_wid * CodeVs.hei_p_size)){// 消えたブロック数が多いものはゴールとして、移行の手を見ない
						return score;
					}else{
						long temp_score = node.getMaxScore();
						if(max_score < temp_score){
							max_score = temp_score;
						}
					}
				}
			}else{
				for(int i=0; i<child.size(); i++){
					ThinkNode2 node = (ThinkNode2) child.get(i);
					if(2*(this.brock_num - node.brock_num) > Math.pow(this.brock_num, 2)/(CodeVs.Field_wid * CodeVs.hei_p_size)){// 消えたブロック数が多いものはゴールとして、移行の手を見ない
						return score;
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
	
	@Override
	public long setChild(){
		long max_score = 0;
		
		float cut = 1;
		
		// 枝切りのパラメータ
		if(brock_num <= 20){
			cut = (float)0.7; 
		}

		for(int x = 1 - CodeVs.Pack_size; x<CodeVs.Field_wid; x++){
			for(int r = 0; r<4; r++){
				if(Math.random() <= cut){
					Field temp = field.clone();
					long raw_score = temp.putPack(CodeVs.pack[this.turn+1].getRotatedPack(r), x);
					if(raw_score > -1){
						ThinkNode node;
						long score = raw_score * Fcp1;
						if(raw_score >= CodeVs.Th){
							node = new ThinkNode2(this.turn+1, temp, score, Fcp1 + 1, x , r);
						}else{
							node = new ThinkNode2(this.turn+1, temp, score, Fcp1, x, r);
						}
						child.add(node);
						if(max_score < score){
							max_score = score;
						}
					}
				}
			}
		}

		return max_score;
	}
	
	public ThinkNode getNextNode(){
		long max_score = -1;
		ThinkNode2 max_child = null;
		
		while(child.size() == 0){
			setChild();
		}
		
		for(int i=0; i<child.size(); i++){
			ThinkNode2 node = (ThinkNode2) child.get(i);
			long temp_score;
			if(2*(this.brock_num - node.brock_num) > Math.pow(this.brock_num, 2)/(CodeVs.Field_wid * CodeVs.hei_p_size)){
//			if((this.brock_num - node.brock_num) < 50){
				temp_score = node.score;
			}else{
				temp_score = node.getMaxScore();
			}
			if(max_score < temp_score){
				max_score = temp_score;
				max_child = (ThinkNode2) child.get(i);
			}
		}
		//String str = String.format("%d %d\n", max_child.x, max_child.r);
//		max_child.printField();
		
		CodeVs.score+=max_child.score;
		
		try {
			CodeVs.filewriter.write(String.format("score = %d\n", CodeVs.score));
			CodeVs.filewriter.write(String.format("Fc + 1 = %d\n", max_child.Fcp1));
			CodeVs.filewriter.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		System.out.printf("%d %d\n", max_child.x, max_child.r);
		System.out.flush();
		return max_child;
	}
	
	
	
}
