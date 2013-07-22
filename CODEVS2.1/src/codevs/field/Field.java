package codevs.field;

import codevs.main.CodeVs;

/**
 * 継承したクラスは clone メソッドをオーバーライドすること
 * @author Keitaro Wakabayashi
 *
 */
public class Field{
	protected byte[][] field;

	public Field(){
		field = new byte[CodeVs.Field_wid][CodeVs.hei_p_size];
	}
	
	protected Field(Field f){
		this();
		for(int x = 0; x<CodeVs.Field_wid; x++){
			for(int y = 0; y<CodeVs.hei_p_size; y++){
				field[x][y] = f.field[x][y];
			}	
		}
	}
	
	public long putPack(byte[][] pack, int x){
		for(int col=0; col<CodeVs.Pack_size; col++){
			for(int row=0; row<CodeVs.Pack_size; row++){
				if(pack[col][row] != 0){
					int xc = x+col;
					if( (xc < 0) || (CodeVs.Field_wid <= xc) || field[xc][row] != 0){
						return -1;
					}
					field[xc][row] = pack[col][row];
				}
			}
		}
		
		long raw_score = 0;
		
		fall();
		int chain = 1;
		while(true){
			int E = eliminate();
			if(E==0){
				break;
			}
			raw_score += score(E,chain); 
			fall();
			chain++;
		}
		
		for(int i=0; i<CodeVs.Field_wid; i++){
			if(field[i][CodeVs.Pack_size] != 0){
				return -1;
			}
		}
		return raw_score;
	}
	
	private void fall(){
		for(int x=0; x<CodeVs.Field_wid; x++){
			byte[] line = new byte[CodeVs.hei_p_size];
			int index = CodeVs.hei_p_size - 1;
			for(int y=index; y>=0; y--){
				byte b = field[x][y];
				if(b != 0){
					line[index--] = b;
				}
			}
			for(int y=CodeVs.hei_p_size - 1; y>=0; y--){
				field[x][y] = line[y];
			}
		}
	}
	
	protected int eliminate(){
		byte[][] temp = copyFieldInfo();
		int E = 0;

		// x,y の傾きを設定し、切片を動かすことによって配列の走査をする
		int[] dif_x = {1, 0, 1, 1};// 方向
		int[] dif_y = {0, 1, 1,-1};// y = (dy/dx)*x + a   // a = dx * y - dy*x
		
		// おじゃまを消すときに使う。ループの中で宣言するより時間短縮になるかもとおもって外側で。
		int[] dir_x = {-1,-1,-1, 0, 0, 1, 1, 1};
		int[] dir_y = { 0,-1, 1,-1, 1, 1,-1, 0};
				
		for(int dir = 0; dir<4; dir++){
			int dx = dif_x[dir];
			int dy = dif_y[dir];
			
			int a_min = (dy > 0) ? -(CodeVs.Field_wid-1) : 0;
			int a_max = dx * (CodeVs.hei_p_size-1) + ((dy < 0) ? (CodeVs.Field_wid-1) : 0);
			
			for(int a = a_min; a<=a_max; a++){
				int y1;
				int x1; 

				if(a<0){
					y1 = 0;
				}else if(a>=CodeVs.hei_p_size){
					y1 = CodeVs.hei_p_size-1;
				}else{
					y1 = a;
				}

				x1 = (y1-a)*dy; 
				// 本当は x1 = (y1-a)*dx/dy となるが、ここを通るときは dx = 1 しかない。
				// dy == 0 のときは x1 = 0 にしたいことと、dy を割ろうが掛けようが正負は一致するため上のようにする。
				// ここまでで
				// a > y_max        ->  y1 = CodeVs.hei_p_size, x1 = a - CodeVs.hei_p_size
				// a < 0            ->  y1 = 0, x1 = -a
				// 0 <= a <= y_max  ->  y1 = a, x1 = 0

				// f[x1][y1] != 0 まで増やす
				loop_1: while(x1<CodeVs.Field_wid && 0 <= y1 && y1<CodeVs.hei_p_size){ // dx >= 0 であるので常に 0 <= x1
					byte f = field[x1][y1];
					if(f == 0 || f == CodeVs.Ojama){
						x1 += dx;
						y1 += dy;
						continue;
					}
					
					int y2 = y1;
					int x2 = x1;

					int sum = field[x1][y1];

					while(true){
						if(sum < CodeVs.SUM){
							y2 += dy;
							x2 += dx;

							if(CodeVs.Field_wid <= x2 || y2 < 0 || CodeVs.hei_p_size <= y2){
								break loop_1; // a の for文 の始めにもどる。while文の後ろに何も書かないこと。書くならラベルを付けて for文まで飛ぶこと。
							}
							byte ff = field[x2][y2];
							if(ff == 0 || ff == CodeVs.Ojama){
								x1 = x2 + dx;
								y1 = y2 + dy;
								break;
							}
							sum += ff;
							// continue; // else の後ろに何も書かない。
						}else if(sum > CodeVs.SUM){
							sum -= field[x1][y1];
							x1 += dx;
							y1 += dy;
							// continue; // else の後ろに何も書かない
						}else{ // sum == CodeVs.SUM
							int x = x1;
							int y = y1;
							
							temp[x][y] = 0;
							E++;
							// 最初は右隣のおじゃまは見ない
							// dif_x > 0 より右隣は後でみるから
							for(int d = 0; d<7; d++){
								int xx = x+dir_x[d];
								int yy = y+dir_y[d];
								if(0<=xx && xx<CodeVs.Field_wid && 0<=yy && yy <CodeVs.hei_p_size && temp[xx][yy] == CodeVs.Ojama){
									temp[xx][yy] = 0;
									E++;
								}
							}
							if(x == x2 && y == y2){
								sum -= field[x1][y1];
								x1+=dx;
								y1+=dy;
								break;
							}
							x += dx;
							y += dy;

							while(true){
								temp[x][y] = 0;
								E++;
								for(int d = 1; d<8; d++){
									int xx = x+dir_x[d];
									int yy = y+dir_y[d];
									if(0<=xx && xx<CodeVs.Field_wid && 0<=yy && yy <CodeVs.hei_p_size && temp[xx][yy] == CodeVs.Ojama){
										temp[xx][yy] = 0;
										E++;
									}
								}
								if(x == x2 && y == y2){
									sum -= field[x1][y1];
									x1+=dx;
									y1+=dy;
									break;
								}
								x += dx;
								y += dy;
							}
						}
					}
				}
			}
		}
		field = temp;
		return E;
	}
	
	private long score(int E, int c){
		int x = E/3;
		if(x <= CodeVs.P){
			return (long) (Math.pow(2, x) * c);
		}else{
			return (long) (Math.pow(2, CodeVs.P) * (x-CodeVs.P+1) * c);
		}
	}
	
	public byte[][] copyFieldInfo(){
		byte[][] f = new byte[CodeVs.Field_wid][CodeVs.hei_p_size];
		for(int x = 0; x<CodeVs.Field_wid; x++){
			for(int y = 0; y<CodeVs.hei_p_size; y++){
				f[x][y] = field[x][y];
			}	
		}
		return f;
	}
	
	public Field clone(){
		return new Field(this);
	}

	public int getBrockNum() {
		int brock_num = 0;
		for(int x = 0; x<CodeVs.Field_wid; x++){
			for(int y = 0; y<CodeVs.hei_p_size; y++){
				if(field[x][y] != 0){
					brock_num++;
				}
			}	
		}
		return brock_num;
	}
}
