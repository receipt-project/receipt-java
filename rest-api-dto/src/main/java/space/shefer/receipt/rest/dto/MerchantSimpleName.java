package space.shefer.receipt.rest.dto;

public enum MerchantSimpleName {
  KARUSEL("КАРУСЕЛЬ", "https://images.app.goo.gl/qsXCpg2eihLbwFqC7"),
  LENTA("ЛЕНТА", "https://images.app.goo.gl/zq5jQrQ6LTbRe9WX7"),
  PEREKRESTOK("ПЕРЕКРЕСТОК", "https://images.app.goo.gl/L2MMepenJQeqM2un6"),
  OKEY("ОКЕЙ", "https://images.app.goo.gl/dQJ841uupaAueuyc7"),
  DIXY("ДИКСИ", "https://images.app.goo.gl/NAfEjeTZHRV6DDBYA"),
  REAL("РЕАЛЪ", "https://images.app.goo.gl/J8SJrWD3ZE7PnxMLA");

  private final String value;
  private final String logoUrl;

  MerchantSimpleName(String value, String logoUrl) {
    this.value = value;
    this.logoUrl = logoUrl;
  }

  public String getValue() {
    return value;
  }

  public String getLogoUrl() {
    return logoUrl;
  }
}
