package codevs.thinknode;

import java.io.IOException;
import java.util.ArrayList;

import codevs.field.Field;
import codevs.main.CodeVs;

public class ThinkNode {
	
	protected Field field;
	protected int turn;
	protected long score;
	
	public int Fcp1;
	
	protected int x;
	protected int r;
	
	protected ArrayList<ThinkNode> child;
	
	public ThinkNode(int turn, Field field, long score, int fcp1, int x, int r){
		this.turn = turn;
		this.field = field;
		this.score = score;
		this.Fcp1 = fcp1;
		this.x = x;
		this.r = r;
		child = new ArrayList<ThinkNode>();
	}
	
	public void setUP(){
//		System.out.printf("turn = %d\n", turn);
		if(turn<2){
			setChild();
			for(int i = 0; i< child.size(); i++){
				child.get(i).setUP();
			}
		}
	}
	
	public long getMaxScore(){
		long max_score = 0;
		if(this.turn < CodeVs.TURN_NUM-1 && this.turn - CodeVs.Turn < 3){				// 読む手数を変えるにはここを変える。setUP()も。
			if(child.size() == 0){
				max_score = setChild();
			}else{
				for(int i=0; i<child.size(); i++){
					long temp_score = child.get(i).getMaxScore();
					if(max_score < temp_score){
						max_score = temp_score;
					}
				}
			}
		}
		return score + max_score;
	}
	
	public long setChild(){
		long max_score = -1;
		for(int x = 1 - CodeVs.Pack_size; x<CodeVs.Field_wid; x++){
			for(int r = 0; r<4; r++){
				Field temp = field.clone();
				long raw_score = temp.putPack(CodeVs.pack[this.turn+1].getRotatedPack(r), x);
				if(raw_score > -1){
					ThinkNode node;
					long score = raw_score * Fcp1;
					if(raw_score >= CodeVs.Th){
						node = new ThinkNode(this.turn+1, temp, score, Fcp1 + 1, x, r);
					}else{
						node = new ThinkNode(this.turn+1, temp, score, Fcp1, x, r);
					}
					child.add(node);
					if(max_score < score){
						max_score = score;
					}
				}
			}
		}
		return max_score;
	}
	
	public ThinkNode getNextNode(){
		long max_score = -1;
		ThinkNode max_child = null;
		
/*		long avg_score = 0;

		for(int i=0; i<child.size(); i++){
			avg_score += child.get(i).node.score;
		}

		avg_score /= child.size();
		System.out.println(avg_score);
	*/	
		for(int i=0; i<child.size(); i++){
			long temp_score = child.get(i).getMaxScore();
			System.out.printf("%10d ", temp_score);
			System.out.printf("%10d ", child.get(i).score);
			System.out.printf("%4d\n", child.get(i).field.getBrockNum());
						
			if(max_score < temp_score){
				max_score = temp_score;
				max_child = child.get(i);
			}
		}
		
		//String str = String.format("%d %d\n", max_child.x, max_child.r);
		max_child.printField();
		
		CodeVs.score+=max_child.score;
		
		try {
			CodeVs.filewriter.write(String.format("score = %d\n", CodeVs.score));
			CodeVs.filewriter.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		
		System.out.printf("%d %d\n", max_child.x, max_child.r);
		System.out.flush();
		return max_child;
	}
	
	public void printField(){
		byte[][] f = field.copyFieldInfo();
		String str = "field " + turn + "\n";
		for(int y=0;y<CodeVs.Field_hei+CodeVs.Pack_size;y++){
			for(int x=0;x<CodeVs.Field_wid;x++){
				str += String.format("%3d", f[x][y]);		
			}
			str += "\n";
		}
//		CodeVs.frame.print(str);
		try {
			CodeVs.filewriter.write(str);
			CodeVs.filewriter.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
/*	class Child{
		ThinkNode node;
		int x;
		int r;
		
		public Child(ThinkNode n, int x, int r){
			node = n;
			this.x = x;
			this.r = r;
		}
	}
	*/
}
