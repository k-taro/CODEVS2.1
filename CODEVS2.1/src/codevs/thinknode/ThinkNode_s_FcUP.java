package codevs.thinknode;

import java.io.IOException;
import java.util.ArrayList;

import codevs.field.Field;
import codevs.main.CodeVs;

public class ThinkNode_s_FcUP extends ThinkNodeFcUP{

	public ThinkNode_s_FcUP(int turn, Field field, long score, int fcp1, int isFire, int x, int r) {
		super(turn, field, score, fcp1, isFire, x, r);
	}
	
	public long setChild(){
		int diff = this.turn - CodeVs.Turn;
		if(this.turn<650){							// 切り替えポイント
			if(diff < 3){
				setChildFcUP();
			}
		}else{
			if(diff < 4){
				setChildNormal();
			}
		}

		return 0;
	}
	
	public ThinkNode getNextNode(){
		ThinkNode2 max_child = null;
		
		if(child.size() == 0){
			setChild();
		}
	
		ThinkNode2 first = (ThinkNode2) child.get(0);
		if(first instanceof ThinkNode_s_FcUP){
			if(((ThinkNode_s_FcUP)first).isFire == 1){
				max_child = first;
			}else{
				int[] min_turn = {CodeVs.TURN_NUM, 0};
				for(int i=0; i<child.size(); i++){
					ThinkNode_s_FcUP node = (ThinkNode_s_FcUP) child.get(i);
					int[] temp = new int[2];
					node.fireTurn(temp);
					if(temp[0] < min_turn[0]){
						min_turn = temp;
						max_child = node;
					}else if(temp[0] == min_turn[0] && temp[1] > min_turn[1]){
						min_turn = temp;
						max_child = node;
					}
				}
				if(min_turn[0] == CodeVs.TURN_NUM){
					max_child = getMaxScoreChild();
				}
			}
		}else{
			max_child = getMaxScoreChild();
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
	
	protected ThinkNode2 getMaxScoreChild(){
		ThinkNode2 max_child = null;
		long max_score = -1;

		for(int i=0; i<child.size(); i++){
			ThinkNode2 node = (ThinkNode2) child.get(i);
			long temp_score;
			if(3*(this.brock_num - node.brock_num) > Math.pow(this.brock_num, 2)/(CodeVs.Field_wid * CodeVs.hei_p_size)){
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
		return max_child;
	}
	
	/**
	 * 発火する最小ターン(第1引数)とそのフィールドのブロック数(第２引数)を返す
	 * 同じターン数ならよりブロック数が多いものが返される。
	 * @param ret
	 * @return
	 */
	protected void fireTurn(final int ret[]){
		int turn_dif = this.turn - CodeVs.Turn;
		if(this.turn < CodeVs.TURN_NUM-1 && turn_dif < 4){
			if(child.size() == 0){
				setChild();
			}
			if(child.size() != 0 && child.get(0) instanceof ThinkNode_s_FcUP){
				if(((ThinkNode_s_FcUP)child.get(0)).isFire == 1){
					ret[0] = this.turn + 1;
					ret[1] = ((ThinkNode_s_FcUP)child.get(0)).brock_num;
					return;
				}else{
					int[] min_turn_ret = {CodeVs.TURN_NUM,0};
					for(int index = 0; index<child.size(); index++){
						ThinkNode_s_FcUP node = (ThinkNode_s_FcUP) child.get(index);
						int temp[] = new int[2];
						node.fireTurn(temp);
						if(min_turn_ret[0]>temp[0]){
							min_turn_ret = temp;
						}else if(min_turn_ret[0] == temp[0] && min_turn_ret[1]<temp[1]){
							min_turn_ret = temp;
						}
					}
					ret[0] = min_turn_ret[0];
					ret[1] = min_turn_ret[1];
					return;
				}
			}
		}
		ret[0] = CodeVs.TURN_NUM;
		ret[1] = 0;
		return;
	}
	
	protected void setChildFcUP(){
		ThinkNode_s_FcUP fireNode = null;
		
		ArrayList<ThinkNode_s_FcUP> temp_list = new ArrayList<ThinkNode_s_FcUP>();
		
		for(int x = 1 - CodeVs.Pack_size; x<CodeVs.Field_wid; x++){
			for(int r = 0; r<4; r++){
				Field temp = field.clone();
				long raw_score = temp.putPack(CodeVs.pack[this.turn+1].getRotatedPack(r), x);
				if(raw_score > -1){
					ThinkNode_s_FcUP node = null;
					long score = raw_score * Fcp1;
					if(raw_score >= CodeVs.Th){
						node = new ThinkNode_s_FcUP(this.turn+1, temp, score, Fcp1 + 1, 1,x,r);
						if(fireNode == null){
							fireNode = node;
						}else{
							if(fireNode.brock_num < node.brock_num){
								fireNode = node;
							}
						}
					}else if(raw_score <= 20 && fireNode == null){
						//else{
						node = new ThinkNode_s_FcUP(this.turn+1, temp, score, Fcp1, 0,x,r);
					}

					if(node != null){
						temp_list.add(node);
					}
				}
			}
		}
		if(fireNode != null){
			child.add(fireNode);
		}else{
			for(int i = 0; i<30; i++){
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
		}
	}
	
	private void setChildNormal(){
		ThinkNode_s_ScoreUP max_child = new ThinkNode_s_ScoreUP(0,new Field(),0,1, 0, 0);
		ArrayList<ThinkNode_s_ScoreUP> temp_list = new ArrayList<ThinkNode_s_ScoreUP>();
		
		for(int x = 1 - CodeVs.Pack_size; x<CodeVs.Field_wid; x++){
			for(int r = 0; r<4; r++){
				Field temp = field.clone();
				long raw_score = temp.putPack(CodeVs.pack[this.turn+1].getRotatedPack(r), x);
				if(raw_score > -1){
					ThinkNode_s_ScoreUP node;
					long score = raw_score * Fcp1;
					if(raw_score >= CodeVs.Th){
						node = new ThinkNode_s_ScoreUP(this.turn+1, temp, score, Fcp1 + 1, x, r);
					}else{
						node = new ThinkNode_s_ScoreUP(this.turn+1, temp, score, Fcp1, x, r);
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

		// 発火するノードがあれば0番目に追加する
		if(max_child.score > 0){
			child.add(max_child);
		}
		
		for(int i = 0; i<17; i++){
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
	}
	
	@Override
	public long getMaxScore(){
		long max_score = 0;
		int turn_dif = this.turn - CodeVs.Turn;
		if(this.turn < CodeVs.TURN_NUM-1 && turn_dif < 4){				// 読む手数を変えるにはここを変える。setUP()も。
			if(child.size() == 0){
				setChild();
				
				if(child.size()  == 0){
					return score;
				}
			}
			
			ThinkNode2 node = (ThinkNode2) child.get(0);
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
				node = (ThinkNode2) child.get(index);
				long temp_score = node.getMaxScore();
				if(max_score < temp_score){
					max_score = temp_score;
				}
			}
		}
		return score + max_score;
	}




}
