package invalid.myask.feelingpeaky.ducks;

public interface IExpandedChunk {
    int getChunkMinY();
    int getChunkMaxY();

    int getNegativeChunkCount();

    int getSubChunkCount();
}
