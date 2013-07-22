package codevs.thinknode;

import java.io.IOException;

import codevs.field.Field;
import codevs.main.CodeVs;

public class ThinkNodeFcUP extends ThinkNode2 {
	protected int isFire;

	public ThinkNodeFcUP(int turn, Field field, long score, int fcp1, int isFire, int x, int r) {
		super(turn, field, score, fcp1, x, r);
		this.isFire = isFire;
	}
	
	public void setUP(){
		setChild();
	}
	
	public long setChild(){
		boolean isFire = false;
		for(int x = 1 - CodeVs.Pack_size; x<CodeVs.Field_wid; x++){
			for(int r = 0; r<4; r++){
				Field temp = field.clone();
				long raw_score = temp.putPack(CodeVs.pack[this.turn+1].getRotatedPack(r), x);
				if(raw_score > -1){
					ThinkNode node;
					long score = raw_score * Fcp1;
					if(this.turn<500){							// 切り替えポイント
						if(raw_score >= CodeVs.Th){
							isFire = true;
							node = new ThinkNodeFcUP(this.turn+1, temp, score, Fcp1 + 1, 1, x, r);
						}else{
							node = new ThinkNodeFcUP(this.turn+1, temp, score, Fcp1, 1, x, r);
						}
					}else{
						if(raw_score >= CodeVs.Th){
							node = new ThinkNode5(this.turn+1, temp, score, Fcp1 + 1, x, r);
						}else{
							node = new ThinkNode5(this.turn+1, temp, score, Fcp1, x, r);
						}						
					}
					child.add(node);
				}
			}
		}
		if(isFire){
			for(int index = 0; index < child.size(); index++){
				if(((ThinkNodeFcUP)child.get(index)).isFire == 0){
					child.remove(index);
					index--;
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
		return 0;
	}
	
	protected int getFireNum(){
		int max_fire_num = 0;
		ThinkNodeFcUP max_fire_child = new ThinkNodeFcUP(0,new Field(),0,1,0,0,0);

		int turn_dif = this.turn - CodeVs.Turn;
		if(this.turn < CodeVs.TURN_NUM-1 && turn_dif < 3){
			if(child.size() == 0){
				setChild();
			}
			if(!(child.get(0) instanceof ThinkNodeFcUP)){
				return this.isFire;
			}

			for(int i = 0; i<child.size(); i++){
				ThinkNodeFcUP fcUPnode = (ThinkNodeFcUP)child.get(i);
				//if(node instanceof ThinkNodeFcUP){
					int temp_fire_num = fcUPnode.getFireNum();

					if(temp_fire_num > max_fire_num){
						max_fire_num = temp_fire_num;
						max_fire_child = (ThinkNodeFcUP) child.get(i);
					}else if(temp_fire_num == max_fire_num){
						ThinkNodeFcUP max_fire_child_node = (ThinkNodeFcUP) max_fire_child;
						if(fcUPnode.isFire == 1){
							if(max_fire_child_node.isFire == 0){
								max_fire_child = (ThinkNodeFcUP) child.get(i);
							}else if(max_fire_child_node.brock_num < fcUPnode.brock_num){
								max_fire_child = (ThinkNodeFcUP) child.get(i);
							}
						}else if(max_fire_child_node.isFire == 0 && max_fire_child_node.brock_num < fcUPnode.brock_num){
							max_fire_child = (ThinkNodeFcUP) child.get(i);
						}
					}
				//}//
			} // end of loop;
		}
		
		return isFire + max_fire_num;
	}
	
	public ThinkNode getNextNode(){
		if(child.size() == 0){
			setChild();
		}
		
		if(!(child.get(0) instanceof ThinkNodeFcUP)){
			return super.getNextNode();
		}
		//else
		
		ThinkNodeFcUP max_fire_child = (ThinkNodeFcUP) child.get(0);
		int max_fire_num = ((ThinkNodeFcUP)max_fire_child).getFireNum();

		for(int i=1; i<child.size(); i++){
			ThinkNodeFcUP fcUPnode = (ThinkNodeFcUP)child.get(i);
			//if(node instanceof ThinkNodeFcUP){
				int temp_fire_num = fcUPnode.getFireNum();

				if(temp_fire_num > max_fire_num){
					max_fire_num = temp_fire_num;
					max_fire_child = (ThinkNodeFcUP) child.get(i);
				}else if(temp_fire_num == max_fire_num){
					ThinkNodeFcUP max_fire_child_node = (ThinkNodeFcUP) max_fire_child;
					if(fcUPnode.isFire == 1){
						if(max_fire_child_node.isFire == 0){
							max_fire_child = (ThinkNodeFcUP) child.get(i);
						}else if(max_fire_child_node.brock_num < fcUPnode.brock_num){
							max_fire_child = (ThinkNodeFcUP) child.get(i);
						}
					}else if(max_fire_child_node.isFire == 0 && max_fire_child_node.brock_num < fcUPnode.brock_num){
						max_fire_child = (ThinkNodeFcUP) child.get(i);
					}
				}
			//}//
		} // end of loop;

		//String str = String.format("%d %d\n", max_child.x, max_child.r);
		max_fire_child.printField();
		
		CodeVs.score+=max_fire_child.score;
		
		try {
			CodeVs.filewriter.write(String.format("score = %d\n", CodeVs.score));
			CodeVs.filewriter.write(String.format("Fc + 1 = %d\n", max_fire_child.Fcp1));
			CodeVs.filewriter.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		System.out.printf("%d %d\n", max_fire_child.x, max_fire_child.r);
		System.out.flush();
		return max_fire_child;
	}

}
