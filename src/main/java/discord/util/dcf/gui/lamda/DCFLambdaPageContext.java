package discord.util.dcf.gui.lamda;

public record DCFLambdaPageContext(int pageNum, int pageSize) {

    public boolean isLastPage() {
        return pageNum + 1 >= pageSize;
    }

    public boolean isFirstPage() {
        return pageNum == 0;
    }
}
