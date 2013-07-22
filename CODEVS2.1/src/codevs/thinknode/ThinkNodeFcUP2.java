package codevs.thinknode;

import java.io.IOException;
import java.util.LinkedList;

import codevs.field.Field;
import codevs.main.CodeVs;

public class ThinkNodeFcUP2 extends ThinkNodeFcUP {
	
	private ThinkNodeFcUP2 parent;
	
	public ThinkNodeFcUP2(int turn, Field field, long score, int fcp1, int isFire, ThinkNodeFcUP2 parent, int x, int r) {
		super(turn, field, score, fcp1, x, r, isFire);
		this.parent = parent;
	}
	
	public long setChild(){
		boolean isFire = false;
		for(int x = 1 - CodeVs.Pack_size; x<CodeVs.Field_wid; x++){
			for(int r = 0; r<4; r++){
				Field temp = field.clone();
				long raw_score = temp.putPack(CodeVs.pack[this.turn+1].getRotatedPack(r), x);
				if(raw_score > -1){
					ThinkNode node = null;
					long score = raw_score * Fcp1;
					if(this.turn<500){							// 切り替えポイント
						if(raw_score >= CodeVs.Th){
							isFire = true;
							node = new ThinkNodeFcUP2(this.turn+1, temp, score, Fcp1 + 1, 1,this,x,r);
						}else if(raw_score <= 20){
						//else{
							node = new ThinkNodeFcUP2(this.turn+1, temp, score, Fcp1, 0,this,x,r);
						}
					}else{
						if(raw_score >= CodeVs.Th){
							node = new ThinkNode5(this.turn+1, temp, score, Fcp1 + 1, x, r);
						}else{
							node = new ThinkNode5(this.turn+1, temp, score, Fcp1, x, r);
						}						
					}
					if(node != null){
						child.add(node);
					}
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

	public ThinkNode getNextNode(){
		if(child.size() == 0){
			setChild();
		}
		
		if(!(child.get(0) instanceof ThinkNodeFcUP)){
			return super.getNextNode();
		}
		//else
		
		LinkedList<ThinkNodeFcUP2> queue = new LinkedList<ThinkNodeFcUP2>();
		
		for(int index = 0; index<child.size(); index++){
			queue.add((ThinkNodeFcUP2) child.get(index));
		}
		
		ThinkNodeFcUP2 shortestChild = new ThinkNodeFcUP2(CodeVs.TURN_NUM-1, new Field(), 0, 1, 1, this, 0, 0);
		
		while(queue.size() > 0){
			ThinkNodeFcUP2 temp_c = queue.poll();
			
			if(temp_c.turn > shortestChild.turn){
				break;
			}
			if(((ThinkNodeFcUP2)temp_c).isFire == 0 && temp_c.turn - this.turn < 3){
				// node の子をキューに追加
				if(temp_c.child.size() == 0){
					temp_c.setChild();
				}
				if(!(temp_c.child.get(0) instanceof ThinkNodeFcUP2)){
					continue;
				}
				for(int index = 0; index<temp_c.child.size(); index++){
					queue.add((ThinkNodeFcUP2)temp_c.child.get(index));
				}
			}
			
			if(((ThinkNodeFcUP2)temp_c).isFire == 1){
				// もうちょい工夫の仕方がありそう
				if(((ThinkNode2)temp_c).brock_num > ((ThinkNode2)shortestChild).brock_num){
					shortestChild = temp_c;
				}
			}
		} // end of loop;

		//String str = String.format("%d %d\n", max_child.x, max_child.r);
		if(shortestChild.turn == CodeVs.TURN_NUM - 1){
			long max_score = -1;
			for(int index = 0; index<child.size(); index++){
				long temp_score = child.get(index).getMaxScore();
				if(max_score < temp_score){
					max_score = temp_score;
					shortestChild = (ThinkNodeFcUP2) child.get(index);
				}
			}
		}
		
		ThinkNodeFcUP2 node = shortestChild;
		
		while(node.parent != this){
			node = node.parent;
		}
		
		node.printField();
		
		CodeVs.score+=node.score;
		
		try {
			CodeVs.filewriter.write(String.format("score = %d\n", CodeVs.score));
			CodeVs.filewriter.write(String.format("Fc + 1 = %d\n", node.Fcp1));
			CodeVs.filewriter.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
				
		System.out.printf("%d %d\n", node.x, node.r);
		System.out.flush();
		
		for(ThinkNode c : child){
			if(c instanceof ThinkNodeFcUP2){
				ThinkNodeFcUP2 temp = (ThinkNodeFcUP2) c;
				temp.parent = null;
			}
		}
		
		return node;
	}

}
