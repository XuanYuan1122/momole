package com.moemoe.lalala.utils;

/**
 * Created by Haru on 2016/4/29 0029.
 */
public class EmailSufix {
    private static void initEmailSufixArray() {
        emailSufixArray = new String[] { "123mail.org", "126.com", "139.com", "163.com", "189.cn", "accountant.com",
                "airpost.net", "alice.it", "aol.co.uk", "aol.com", "asia.com", "att.net", "ausi.com", "australia.edu",
                "biglobe.ne.jp", "bigpond.com", "bigpond.net.au", "blueyonder.co.uk", "btinternet.com",
                "btopenworld.com", "cairns.net.au", "canada-11.com", "care2.com", "cheerful.com", "comcast.net",
                "compuserve.com", "consultant.com", "contractor.net", "coolkiwi.co.nz", "coolkiwi.com ", "cs.com",
                "cwgsy.net", "daum.com", "disney.ne.jp", "docomo.ne.jp", "doctor.com", "dr.com", "dwmail.jp",
                "earthlink.net", "email.com", "email.it", "eml.cc", "empas.com", "engineer.com", "es.co.nz",
                "europe.com", "excite.co.jp", "ezweb.ne.jp", "fast-email.com", "fast-email.org", "fastemail.us",
                "fastmail.ca", "fastmail.cn", "fastmail.co.uk", "fastmail.com.au", "fastmail.es", "fastmail.fm",
                "fastmail.in", "fastmail.jp", "fastmail.net", "fastmail.to", "fastmail.us", "fmail.co.uk",
                "foxmail.com", "freeolamail.com", "fsmail.net", "gmail.com", "gmx.de", "goowy.com", "hanafos.com",
                "hanmail.net", "HK.com", "hkboy.org", "hkgirl.org", "hongkong.com", "hotmail.co.jp", "hotmail.co.uk",
                "hotmail.com", "hotmail.com.uk", "hotmail.de", "hotmail.fr", "hutchcity.com", "i-cable.com",
                "iespana.es", "ihug.co.nz", "iname.com", "inbox.com", "infoseek.jp", "intsig.com", "kiss99.com",
                "korea.com", "korea.net", "lawyer.com", "libero.it", "linuxmail.org", "live.com", "live.hk",
                "livedoor.com", "london.com", "lycos.co.kr", "lycos.com", "mail.com", "mail.goo.ne.jp",
                "mail2000.com.tw", "mopera.net", "ms2.hinet.net", "ms24.hinet.net", "ms64.hinet.net", "msa.hinet.net",
                "myself.com", "mysinamail.com", "myway.com", "naver.com", "netscape.ca", "netscape.net", "netsgo.com",
                "netvigator.com", "netzero.net", "ntlworld.com", "ole.com", "paran.com", "pcconnec.co.nz",
                "pchome.com.tw", "pie.com.tw", "post.com", "pre.sltnet.lk", "qq.com", "quik.co.nz", "rocketmail.com",
                "sbcglobal.com", "seed.net.tw", "sina.cn", "sina.com", "sinababy.com", "sinagirl.com", "sinaman.com",
                "sinasexy.com", "sinatown.com", "slt.lk", "sltnet.lk", "sohu.com", "talk21.com", "talktalk.net",
                "techie.com", "telstra.com", "terra.es", "tim.it", ",tin.it", "tiscali.co.uk", "tiscali.it", "tom.com",
                "t-online.de", "tudominio.com", "tudominio.es", "tudominio.org", "usa.com", "uymail.com",
                "verizon.net", "vip.qq.com", "vip.sina.com", "vip.tom.com", "virgilio.it", "virgin.net",
                "virginmedia.com", "vodafone.co.nz", "voila.fr", "warwick.net", "wave.co.nz", "web.de",
                "westnet.com.au", "wo.com.cn", "writeme.com", "xtra.co.nz", "y7mail.com", "yahoo.ca", "yahoo.cn",
                "yahoo.co.jp", "yahoo.co.kr", "yahoo.co.nz", "yahoo.co.uk", "yahoo.com", "yahoo.com.au",
                "yahoo.com.hk", "yahoo.com.tw", "yahoo.es", "yahoo.fr", "yahoo.it", "yam.com", "ymail.com",
                "yopmail.com" };// 189
    }

    public static String[] emailSufixArray;

    public static String[] getFormatedEmails(String emailPrefix) {
        String[] emails = null;
        if (emailSufixArray == null) {
            initEmailSufixArray();
        }
        int size = emailSufixArray.length;
        emails = new String[size];
        for (int index = 0; index < size; ++index) {
            emails[index] = emailPrefix + emailSufixArray[index];
        }
        return emails;
    }
}
