public class Md5Test {
    public static void main(String[] a) throws Exception {
        for (String s : new String[]{"", "a", "abc", "0065557", "8531", "message digest"}) {
            byte[] h = game.b.a(s);
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] r = md.digest(s.getBytes("UTF-8"));
            System.out.println(s + " -> " + game.b.b(h) + (java.util.Arrays.equals(h, r) ? "  OK" : "  WRONG (expected " + game.b.b(r) + ")"));
        }
        System.out.println("an.a() = " + game.an.a());
    }
}
