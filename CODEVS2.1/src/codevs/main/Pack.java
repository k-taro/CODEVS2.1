package codevs.main;

public class Pack {

	private final byte[][][] pack;
	
	public Pack(byte[][] pack){
		this.pack = new byte[4][][];
		this.pack[0] = pack;
		
		for(int i=1; i<4; i++){
			this.pack[i] = new byte[CodeVs.Pack_size][CodeVs.Pack_size];

			for(int y=0; y<CodeVs.Pack_size; y++){
				for(int x=0; x<CodeVs.Pack_size; x++){
					this.pack[i][x][y] = this.pack[i-1][y][CodeVs.Pack_size-1-x];
					///////  右回転  /////////
					// pack[i]   pack[i-1]//
					//--------------------//
					//  1 2 3      3 6 9  //
					//  4 5 6  <-  2 5 8  //
					//  7 8 9      1 4 7  //
					////////////////////////
				}
			}
		}
	}
	
	public byte[][] getRotatedPack(int r){
		return pack[r];
	}
}
