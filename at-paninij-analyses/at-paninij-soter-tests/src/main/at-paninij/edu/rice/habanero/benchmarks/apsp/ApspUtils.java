package edu.rice.habanero.benchmarks.apsp;

import static edu.rice.habanero.benchmarks.apsp.ApspConfig.B;
import static edu.rice.habanero.benchmarks.apsp.ApspConfig.N;
import static edu.rice.habanero.benchmarks.apsp.ApspConfig.W;

import java.util.Random;

public class ApspUtils
{
    private static long[][] _graphData;
    
    static {
        Random random = new Random();
        long[][] localData = new long[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = i+1; j < N; j++) {
                int r = random.nextInt(W);
                localData[i][j] = r;
                localData[j][i] = r;
            }
        }

        _graphData = localData;
    }
    
    public static long[][] graphData() {
        return _graphData;
    }
    
    public static long[][] getBlock(long[][] srcData, int myBlockId)
    {
        long[][] localData = new long[B][B];
        
        int numBlocksPerDim = N / B;
        int globalStartRow = (myBlockId / numBlocksPerDim) * B;
        int globalStartCol = (myBlockId % numBlocksPerDim) * B;
        
        for (int i = 0; i < B; i++) {
            for (int j = 0; j < B; j++) {
                localData[i][j] = srcData[i + globalStartRow][j + globalStartCol];
            }
        }
        
        return localData;
    }
}
