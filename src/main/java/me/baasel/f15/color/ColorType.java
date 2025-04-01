package me.baasel.f15.color;

public enum ColorType {

    RED("1356649811004227754"),
    BLUE("1356649886434721973"),
    BLACK("1356649942806040859"),
    PLATINUM("1356650410177462425"),
    CARBON("1356650494025662545"),
    TITANIUM("1356650525138878534"),
    MIDNIGHT("1356650573633687759"),
    SHADOW("1356650625269760041"),
    NAVY("1356650707062620161"),
    STEEL("1356650749664432259");

    private final String roleId;

    ColorType(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleId() {
        return roleId;
    }
}