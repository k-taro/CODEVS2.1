package codevs.field;

import codevs.main.CodeVs;

public class Field0 {

	private byte[][] field;
	
	public Field0(){
		field = new byte[CodeVs.Field_wid][CodeVs.hei_p_size];
	}
	
	private Field0(byte[][] f){
		this();
		for(int x = 0; x<CodeVs.Field_wid; x++){
			for(int y = 0; y<CodeVs.hei_p_size; y++){
				field[x][y] = f[x][y];
			}	
		}
	}
	
	public long putPack(byte[][] pack, int x, int Fcp1){
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
		return raw_score * (Fcp1) ;
	}
	
	public void fall(){
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
	
	public int eliminate(){
		byte[][] temp = copyFieldInfo();
		int E = 0;
		
		// ベクトルと媒介変数で配列の走査をする
		// x = cos * s - sin * t
		// y = sin * s + cos * t
		
		int[] cos_ = {1, 0, 1, 1};// 方向ベクトルを表現している。大きさを変えた回転行列と見ることができるのでcos,sinという変数名を使っている。 
		int[] sin_ = {0, 1, 1,-1}; // theta/pi = 0, 1/2, 1/4, -1/4
		
		for(int dir = 0; dir<4; dir++){
			int cos = cos_[dir];
			int sin = sin_[dir];
			int abs = Math.abs(cos) + Math.abs(sin); // 行列式。 1 か 0 なので2乗しなくても良い
			// s = ( cos * x + sin * y)/abs
			// t = (-sin * x + cos * y)/abs
			// ? 逆行列なので本来なら上の式のように行列式で割らなければならないが、 s, t を計算速度の向上のため整数値にしたいので 行列式を掛けて x, y に変換するときに割ることとする。
			// ? こうすることによってインクリメントは 1 ずつ
			// |cos|, |sin| は 0,1 のみであるためかなり簡略化してあるので注意
			// 比較も 0 の場合を上手く分けてるので注意
			int s_min = ((cos < 0 ? -(CodeVs.Field_wid-1) : 0) + (sin < 0 ? -(CodeVs.hei_p_size-1) : 0));
			int s_max = ((cos > 0 ? (CodeVs.Field_wid-1) : 0) + (sin > 0 ? (CodeVs.hei_p_size-1) : 0));
			
			for(int s=s_min; s<=s_max; s++){
				// x_min <= cos*s - sin*t <= x_max-1  ->  cos*s - (x_max-1) <= sin*t <= cos*s - x_min
				// y_min <= sin*s + cos*t <= y_max-1  ->      y_min - sin*s <= cos*t <= (y_max-1) - sin*s 
				int t_min;
				int t_max;
				switch (dir){
				case 0:
					t_min = 0;
					t_max = (CodeVs.hei_p_size - 1);
					break;
				case 1:
					t_min = -(CodeVs.Field_wid - 1);
					t_max = 0;
					break;
				case 2:
					t_min = s - (CodeVs.Field_wid-1);
					int c = -s;
					if(t_min < c){ t_min = c; }
					t_max =  s;
					c = (CodeVs.hei_p_size-1) - s;
					if(t_max > c){ t_max = c; }
					break;
				default: // case 3:
					t_min = (s>-s) ? s:-s;
					t_max = (CodeVs.Field_wid-1) - s;
					c = (CodeVs.hei_p_size-1) + s;
					if(t_max > c){ t_max = c; }
					break;
				}
				
				for(int t1 = t_min; t1<t_max; t1++){
					int x1 = (cos*s - sin*t1);
					int y1 = (sin*s + cos*t1);

					if(field[x1][y1] == 0){
						continue;
					}
					
					int t2 = t1+abs;
					int x2 = (cos*s - sin*t2);
					int y2 = (sin*s + cos*t2);
					int sum = field[x1][y1] + field[x2][y2];
					while(true){
						if(sum < CodeVs.SUM){
							if(t2 < t_max){
								t2++;
								x2 -= sin;
								y2 += cos;
								byte f = field[x2][y2];
								if(f == 0 || f == CodeVs.Ojama){
									t1 = t2;
									break;
								}
								sum += f;
								continue;// 書かなくてもよいか？
							}else{
								// t2 > t_max の処理
								t1 = t2;
								break;
							}
						}else if(sum > CodeVs.SUM){
							//sum -= field[x1][y1];
							sum -= field[(cos*s - sin*t1)][(sin*s + cos*t1)];
							t1++;
							continue;// 書かなくてもよいか？
						}else{ // sum == CodeVs.SUM
							for(int t : new int[] {t1-1, t2+1}){
								// おじゃまは重複して数えられない？ため temp で比較する
								if(t_min <= t && t<=t_max){
									int x = (cos*(s-1) - sin*t);
									int y = (sin*(s-1) + cos*t);
									if(0<=x && x<CodeVs.Field_wid && 0<=y && y<CodeVs.hei_p_size && temp[x][y] == CodeVs.Ojama){
										temp[x][y] = 0;
										E++;
									}
									x += cos;
									y += sin;
									if(temp[x][y] == CodeVs.Ojama){
										temp[x][y] = 0;
										E++;
									}
									x += cos;
									y += sin;
									if(0<=x && x<CodeVs.Field_wid && 0<=y && y<CodeVs.hei_p_size && temp[x][y] == CodeVs.Ojama){
										temp[x][y] = 0;
										E++;
									}
								}
							}
							
							for(int t = t1; t<=t2; t++){
								int x = (cos*(s-1) - sin*t);
								int y = (sin*(s-1) + cos*t);
								if(0<=x && x<CodeVs.Field_wid && 0<=y && y<CodeVs.hei_p_size && temp[x][y] == CodeVs.Ojama){// [x][y]が存在しうるかに変える
									temp[x][y] = 0;
									E++;
								}
								
								x += cos;
								y += sin;
								temp[x][y] = 0;
								E++;
								
								x += cos;
								y += sin;
								if(0<=x && x<CodeVs.Field_wid && 0<=y && y<CodeVs.hei_p_size && temp[x][y] == CodeVs.Ojama){
									temp[x][y] = 0;
									E++;
								}
							}
							t2++;
							if(t2 <= t_max){
								x2 = (cos*s - sin*t2);
								y2 = (sin*s + cos*t2);
								byte f = field[x2][y2];
								if(f == 0 || f == CodeVs.Ojama){
									t1 = t2;
									break;
								}
								sum += f;
								sum -= field[(cos*s - sin*t1)][(sin*s + cos*t1)];
								t1++;
							}else{
								t1 = t2;
								break;
							}
						}
					}
					// 何もしないで
				}
			}
		}

		field = temp;
		return E;
	}
	
	public long score(int E, int c){
		int x = E/3;
		if(x <= CodeVs.P){
			return 2^(x) * c;
		}else{
			return 2^(CodeVs.P) * (x-CodeVs.P+1) * c;
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
	
	public Field0 clone(){
		return new Field0(field);
	}
}
