package codevs.thinknode;

import codevs.field.Field;
import codevs.main.CodeVs;

public class ThinkNode3 extends ThinkNode2 {

	public ThinkNode3(int turn, Field field, long score, int fcp1, int x, int r) {
		super(turn, field, score, fcp1, x, r);
	}
	
	@Override
	public long setChild(){
		ThinkNode3 max_child = new ThinkNode3(0,new Field(),0,1,0,0);
		
		for(int x = 1 - CodeVs.Pack_size; x<CodeVs.Field_wid; x++){
			for(int r = 0; r<4; r++){
				Field temp = field.clone();
				long raw_score = temp.putPack(CodeVs.pack[this.turn+1].getRotatedPack(r), x);
				if(raw_score > -1){
					ThinkNode3 node;
					long score = raw_score * Fcp1;
					if(raw_score >= CodeVs.Th){
						node = new ThinkNode3(this.turn+1, temp, score, Fcp1 + 1, x, r);
					}else{
						node = new ThinkNode3(this.turn+1, temp, score, Fcp1, x, r);
					}
					child.add(node);
					if(max_child.score < score){
						max_child = node;
					}
				}
			}
		}
		
		// 枝切りのパラメータ
		if(brock_num < 100){
			int size = child.size();
			for(int index = 0; index < size*(100-brock_num)/250.0; index ++){
				ThinkNode3 temp_c = (ThinkNode3) child.get((int)(Math.random() * (child.size() - 0.01)));
				if(temp_c != max_child){
					child.remove(temp_c);
				}
			}
		}

		return max_child.score;
	}
}
