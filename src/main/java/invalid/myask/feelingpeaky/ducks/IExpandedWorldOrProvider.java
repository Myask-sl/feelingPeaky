package invalid.myask.feelingpeaky.ducks;

public interface IExpandedWorldOrProvider {
    int getWorldMinY();
    int getWorldMaxY();

    int getSubChunkCount();

    int getNegativeChunkCount();

    int getWorldSpawnMinY();
    int getWorldSpawnMaxY();

    double getKillPLaneY();
}
