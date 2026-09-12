package com.chhawa.pratishthan.varganimanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends android.app.Activity {
    private static final String PREFS = "vargani_data";
    private static final String VARGANI = "vargani";
    private static final String EXPENSES = "expenses";
    private static final String LANG = "marathi_ui";

    private SharedPreferences prefs;
    private LinearLayout root;
    private boolean marathi;
    private String currentScreen = "home";

    private final int BG = Color.rgb(247, 244, 240);
    private final int DARK = Color.rgb(43, 31, 25);
    private final int DARK2 = Color.rgb(65, 45, 35);
    private final int ORANGE = Color.rgb(232, 126, 42);
    private final int GREEN = Color.rgb(25, 132, 78);
    private final int RED = Color.rgb(190, 58, 50);
    private final int TEXT = Color.rgb(45, 40, 37);
    private final int MUTED = Color.rgb(113, 105, 99);
    private final int WHITE = Color.WHITE;
    private final int BORDER = Color.rgb(226, 220, 214);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        marathi = prefs.getBoolean(LANG, false);
        setupWindowInsets();
        showHome();
    }

    private void setupWindowInsets() {
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= 21) {
            w.setStatusBarColor(DARK);
            w.setNavigationBarColor(DARK);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            w.getDecorView().setSystemUiVisibility(0);
        }
    }

    private void createRoot() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(DARK);
        root.setFitsSystemWindows(false);
        setContentView(root);
        if (Build.VERSION.SDK_INT >= 21) {
            root.setOnApplyWindowInsetsListener((v, insets) -> {
                int top, bottom;
                if (Build.VERSION.SDK_INT >= 30) {
                    android.graphics.Insets i = insets.getInsets(WindowInsets.Type.systemBars());
                    top = i.top;
                    bottom = i.bottom;
                } else {
                    top = insets.getSystemWindowInsetTop();
                    bottom = insets.getSystemWindowInsetBottom();
                }
                v.setPadding(0, top, 0, bottom);
                return insets;
            });
            root.requestApplyInsets();
        }
    }

    private String tr(String en, String mr) { return marathi ? mr : en; }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView tv(String text, float sp, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(text);
        v.setTextSize(sp);
        v.setTextColor(color);
        v.setTypeface(Typeface.DEFAULT, bold ? Typeface.BOLD : Typeface.NORMAL);
        v.setGravity(Gravity.CENTER_VERTICAL);
        return v;
    }

    private LinearLayout row() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    private LinearLayout col() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    private LinearLayout.LayoutParams lp(int w, int h) { return new LinearLayout.LayoutParams(w, h); }
    private LinearLayout.LayoutParams lp(int w, int h, float weight) { return new LinearLayout.LayoutParams(w, h, weight); }

    private void margin(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) v.getLayoutParams();
            p.setMargins(dp(l), dp(t), dp(r), dp(b));
            v.setLayoutParams(p);
        }
    }

    private GradientDrawable bg(int color, float radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    private GradientDrawable strokeBg(int fill, int stroke, float radius) {
        GradientDrawable d = bg(fill, radius);
        d.setStroke(dp(1), stroke);
        return d;
    }

    private Button actionButton(String text, int background, int textColor) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(14);
        b.setTextColor(textColor);
        b.setAllCaps(false);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setMinHeight(dp(48));
        b.setPadding(dp(12), 0, dp(12), 0);
        b.setBackground(bg(background, 14));
        return b;
    }

    private EditText input(String hint, int inputType) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setTextSize(15);
        e.setTextColor(TEXT);
        e.setHintTextColor(Color.rgb(155, 147, 140));
        e.setSingleLine(true);
        e.setInputType(inputType);
        e.setPadding(dp(14), 0, dp(14), 0);
        e.setBackground(strokeBg(WHITE, BORDER, 12));
        e.setMinHeight(dp(52));
        return e;
    }

    private TextView sectionTitle(String text) {
        TextView t = tv(text, 18, TEXT, true);
        t.setPadding(dp(2), dp(8), dp(2), dp(8));
        return t;
    }

    private void addSpacer(LinearLayout parent, int height) {
        SpaceView s = new SpaceView(this);
        parent.addView(s, lp(1, dp(height)));
    }

    private static class SpaceView extends View { SpaceView(Context c) { super(c); } }

    private LinearLayout topBar(String title, boolean back, View.OnClickListener rightAction, String rightText) {
        LinearLayout bar = new LinearLayout(this);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(16), dp(10), dp(12), dp(10));
        bar.setBackground(bg(DARK, 0));
        if (back) {
            TextView backV = tv("‹", 34, WHITE, false);
            backV.setGravity(Gravity.CENTER);
            bar.addView(backV, lp(dp(42), dp(48)));
            backV.setOnClickListener(v -> showHome());
        }
        TextView titleV = tv(title, 19, WHITE, true);
        bar.addView(titleV, lp(0, dp(48), 1));
        if (rightAction != null) {
            TextView r = tv(rightText, 14, Color.rgb(255, 219, 177), true);
            r.setGravity(Gravity.CENTER);
            r.setPadding(dp(8), 0, dp(8), 0);
            bar.addView(r, lp(dp(90), dp(48)));
            r.setOnClickListener(rightAction);
        }
        return bar;
    }

    private void showHome() {
        currentScreen = "home";
        createRoot();

        LinearLayout header = col();
        header.setPadding(dp(18), dp(14), dp(18), dp(18));
        header.setBackground(bg(DARK, 0));

        LinearLayout first = row();
        TextView menu = tv("☰", 25, WHITE, false);
        menu.setGravity(Gravity.CENTER);
        first.addView(menu, lp(dp(44), dp(42)));
        menu.setOnClickListener(v -> showMenu());

        LinearLayout brand = col();
        TextView app = tv("VARGANI MANAGER", 11, Color.rgb(255, 201, 150), true);
        TextView mandal = tv("🚩 । छावा प्रतिष्ठान । 🚩", 19, WHITE, true);
        brand.addView(app);
        brand.addView(mandal);
        first.addView(brand, lp(0, dp(48), 1));

        TextView lang = tv(marathi ? "EN" : "मराठी", 13, WHITE, true);
        lang.setGravity(Gravity.CENTER);
        lang.setBackground(strokeBg(Color.TRANSPARENT, Color.rgb(116, 91, 75), 10));
        first.addView(lang, lp(dp(60), dp(38)));
        lang.setOnClickListener(v -> {
            marathi = !marathi;
            prefs.edit().putBoolean(LANG, marathi).apply();
            showHome();
        });
        header.addView(first);

        TextView greet = tv(tr("Manage every contribution in one place", "सर्व वर्गणीचा हिशोब एकाच ठिकाणी सांभाळा"), 13, Color.rgb(224, 211, 201), false);
        header.addView(greet);
        root.addView(header, lp(-1, dp(116)));

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);
        scroll.setFillViewport(true);
        LinearLayout content = col();
        content.setPadding(dp(16), dp(14), dp(16), dp(22));
        scroll.addView(content, lp(-1, -2));

        LinearLayout announce = row();
        announce.setPadding(dp(14), dp(10), dp(14), dp(10));
        announce.setBackground(bg(Color.rgb(255, 244, 226), 14));
        TextView a = tv("📣  " + tr("Record each contribution and keep the mandal account clear.", "प्रत्येक वर्गणीची नोंद करा आणि मंडळाचा हिशोब स्पष्ट ठेवा."), 13, Color.rgb(121, 77, 31), true);
        announce.addView(a, lp(-1, -2));
        content.addView(announce, lp(-1, -2));
        addSpacer(content, 14);

        double total = totalVargani();
        double expenses = totalExpenses();
        double balance = total - expenses;
        double cash = modeTotal("Cash");
        double online = modeTotal("Online");

        LinearLayout stats = row();
        stats.addView(statCard(tr("Total Vargani", "एकूण वर्गणी"), money(total), GREEN, "₹"), lp(0, dp(112), 1));
        stats.addView(statCard(tr("Expenses", "खर्च"), money(expenses), RED, "−"), lp(0, dp(112), 1));
        content.addView(stats, lp(-1, dp(112)));
        addSpacer(content, 10);

        LinearLayout balanceCard = col();
        balanceCard.setPadding(dp(16), dp(14), dp(16), dp(14));
        balanceCard.setBackground(bg(DARK2, 18));
        TextView bl = tv(tr("Remaining Balance", "शिल्लक रक्कम"), 13, Color.rgb(220, 204, 193), true);
        TextView bv = tv(money(balance), 28, WHITE, true);
        TextView bs = tv(tr("Collection minus expenses", "वर्गणी वजा खर्च"), 12, Color.rgb(200, 184, 172), false);
        balanceCard.addView(bl); balanceCard.addView(bv); balanceCard.addView(bs);
        content.addView(balanceCard, lp(-1, dp(112)));
        addSpacer(content, 10);

        LinearLayout modes = row();
        modes.addView(modeCard("💵", tr("Cash", "रोख"), money(cash), GREEN), lp(0, dp(84), 1));
        modes.addView(modeCard("📱", tr("Online", "ऑनलाइन"), money(online), ORANGE), lp(0, dp(84), 1));
        content.addView(modes, lp(-1, dp(84)));
        addSpacer(content, 16);

        TextView quick = sectionTitle(tr("Quick Actions", "जलद कृती"));
        content.addView(quick);
        LinearLayout actions1 = row();
        Button addV = actionButton("＋ " + tr("Add Vargani", "वर्गणी नोंदवा"), GREEN, WHITE);
        Button addE = actionButton("＋ " + tr("Add Expense", "खर्च नोंदवा"), ORANGE, WHITE);
        actions1.addView(addV, lp(0, dp(56), 1));
        actions1.addView(addE, lp(0, dp(56), 1));
        content.addView(actions1, lp(-1, dp(56)));
        addV.setOnClickListener(v -> showAddVargani());
        addE.setOnClickListener(v -> showAddExpense());
        addSpacer(content, 12);

        LinearLayout actions2 = row();
        Button contributors = actionButton("👥  " + tr("Contributors", "वर्गणीदार"), WHITE, TEXT);
        Button expensesBtn = actionButton("🧾  " + tr("Expenses", "खर्च"), WHITE, TEXT);
        actions2.addView(contributors, lp(0, dp(52), 1));
        actions2.addView(expensesBtn, lp(0, dp(52), 1));
        content.addView(actions2, lp(-1, dp(52)));
        contributors.setOnClickListener(v -> showContributors());
        expensesBtn.setOnClickListener(v -> showExpenses());
        addSpacer(content, 10);

        Button reports = actionButton("📊  " + tr("Reports & Account Summary", "अहवाल आणि हिशोब"), WHITE, TEXT);
        content.addView(reports, lp(-1, dp(54)));
        reports.setOnClickListener(v -> showReports());

        root.addView(scroll, lp(-1, 0, 1));

        LinearLayout bottom = row();
        bottom.setGravity(Gravity.CENTER);
        bottom.setPadding(dp(8), dp(7), dp(8), dp(7));
        bottom.setBackground(bg(WHITE, 0));
        Button homeBtn = actionButton("⌂  " + tr("Home", "मुख्य"), DARK, WHITE);
        Button addBtn = actionButton("＋  " + tr("Vargani", "वर्गणी"), GREEN, WHITE);
        Button reportBtn = actionButton("▤  " + tr("Reports", "अहवाल"), WHITE, TEXT);
        bottom.addView(homeBtn, lp(0, dp(44), 1));
        bottom.addView(addBtn, lp(0, dp(44), 1));
        bottom.addView(reportBtn, lp(0, dp(44), 1));
        root.addView(bottom, lp(-1, dp(58)));
        addBtn.setOnClickListener(v -> showAddVargani());
        reportBtn.setOnClickListener(v -> showReports());
    }

    private LinearLayout statCard(String title, String value, int accent, String icon) {
        LinearLayout c = col();
        c.setPadding(dp(13), dp(10), dp(13), dp(10));
        c.setBackground(bg(WHITE, 16));
        TextView top = tv(icon + "  " + title, 12, MUTED, true);
        TextView val = tv(value, 20, accent, true);
        c.addView(top); c.addView(val);
        return c;
    }

    private LinearLayout modeCard(String icon, String title, String value, int accent) {
        LinearLayout c = col();
        c.setPadding(dp(13), dp(8), dp(13), dp(8));
        c.setBackground(bg(WHITE, 14));
        TextView line = tv(icon + "  " + title, 12, MUTED, true);
        TextView val = tv(value, 18, accent, true);
        c.addView(line); c.addView(val);
        LinearLayout.LayoutParams p = lp(0, dp(84), 1);
        p.setMargins(dp(4), 0, dp(4), 0);
        c.setLayoutParams(p);
        return c;
    }

    private void showMenu() {
        String[] items = {
                tr("Add Vargani", "वर्गणी नोंदवा"),
                tr("Add Expense", "खर्च नोंदवा"),
                tr("Contributors", "वर्गणीदार"),
                tr("Expenses", "खर्च"),
                tr("Reports", "अहवाल")
        };
        new AlertDialog.Builder(this)
                .setTitle(tr("Mandal Menu", "मंडळ मेनू"))
                .setItems(items, (d, which) -> {
                    if (which == 0) showAddVargani();
                    else if (which == 1) showAddExpense();
                    else if (which == 2) showContributors();
                    else if (which == 3) showExpenses();
                    else showReports();
                }).show();
    }

    private void showAddVargani() { showVarganiForm(-1); }

    private void showVarganiForm(int editIndex) {
        currentScreen = editIndex >= 0 ? "editVargani" : "addVargani";
        createRoot();
        root.addView(topBar(tr(editIndex >= 0 ? "Edit Vargani" : "Add Vargani", editIndex >= 0 ? "वर्गणी संपादित करा" : "वर्गणी नोंदवा"), true, null, ""), lp(-1, dp(68)));
        ScrollView scroll = new ScrollView(this); scroll.setBackgroundColor(BG);
        LinearLayout c = col(); c.setPadding(dp(16), dp(14), dp(16), dp(24)); scroll.addView(c);

       JSONArray all = getArray(VARGANI);
        final JSONObject existing = (editIndex >= 0 && editIndex < all.length())
        ? all.optJSONObject(editIndex)
        : null;

        c.addView(sectionTitle(tr("Contributor Details", "वर्गणीदाराची माहिती")));
        EditText name = input(tr("Full name *", "पूर्ण नाव *"), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        EditText phone = input(tr("Mobile number *", "मोबाईल नंबर *"), InputType.TYPE_CLASS_PHONE);
        c.addView(name, lp(-1, dp(54))); addSpacer(c, 10);
        c.addView(phone, lp(-1, dp(54))); addSpacer(c, 14);

        c.addView(sectionTitle(tr("Amount", "रक्कम")));
        EditText amount = input(tr("Enter amount *", "रक्कम टाका *"), InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        c.addView(amount, lp(-1, dp(54))); addSpacer(c, 10);
        HorizontalScrollView qs = new HorizontalScrollView(this); qs.setHorizontalScrollBarEnabled(false);
        LinearLayout qrow = row();
        for (String q : new String[]{"101","251","501","1001","2100"}) {
            Button qb = actionButton("₹ " + q, WHITE, TEXT); LinearLayout.LayoutParams qp=lp(dp(82),dp(44)); qp.setMargins(0,0,dp(8),0); qrow.addView(qb,qp); qb.setOnClickListener(v->amount.setText(q));
        }
        qs.addView(qrow); c.addView(qs, lp(-1, dp(48))); addSpacer(c, 14);

        c.addView(sectionTitle(tr("Payment Mode", "पेमेंट प्रकार")));
        RadioGroup mode = new RadioGroup(this); mode.setOrientation(RadioGroup.HORIZONTAL);
        RadioButton cash = new RadioButton(this); cash.setText(tr("Cash", "रोख")); cash.setTextSize(15); cash.setTextColor(TEXT);
        RadioButton online = new RadioButton(this); online.setText(tr("Online", "ऑनलाइन")); online.setTextSize(15); online.setTextColor(TEXT);
        mode.addView(cash, lp(0,dp(50),1)); mode.addView(online,lp(0,dp(50),1)); cash.setChecked(true); c.addView(mode,lp(-1,dp(50))); addSpacer(c,8);

        c.addView(sectionTitle(tr("Date (optional)", "दिनांक (ऐच्छिक)")));
        EditText date=input(tr("Tap to choose a date", "दिनांक निवडण्यासाठी टॅप करा"),InputType.TYPE_CLASS_TEXT); date.setFocusable(false); c.addView(date,lp(-1,dp(54))); date.setOnClickListener(v->pickDate(date)); addSpacer(c,12);
        c.addView(sectionTitle(tr("Note (optional)", "नोंद (ऐच्छिक)")));
        EditText note=input(tr("Any note", "काही नोंद"),InputType.TYPE_CLASS_TEXT); c.addView(note,lp(-1,dp(54))); addSpacer(c,18);

        if (existing != null) {
            name.setText(existing.optString("name")); phone.setText(existing.optString("phone")); amount.setText(formatAmount(existing.optDouble("amount",0)));
            date.setText(existing.optString("date")); note.setText(existing.optString("note"));
            if ("Online".equalsIgnoreCase(existing.optString("mode"))) online.setChecked(true); else cash.setChecked(true);
        }

        Button save=actionButton("✓  "+tr(editIndex>=0?"Update Vargani":"Save Vargani",editIndex>=0?"वर्गणी अपडेट करा":"वर्गणी सेव्ह करा"),GREEN,WHITE); c.addView(save,lp(-1,dp(56)));
        save.setOnClickListener(v->{
            String n=name.getText().toString().trim(), ph=phone.getText().toString().trim(), am=amount.getText().toString().trim();
            if(n.isEmpty()){error(name,tr("Enter the name","नाव टाका"));return;}
            if(digits(ph).length()<10){error(phone,tr("Enter a valid mobile number","योग्य मोबाईल नंबर टाका"));return;}
            double val=parse(am); if(val<=0){error(amount,tr("Enter a valid amount","योग्य रक्कम टाका"));return;}
            try {
                JSONObject obj=existing==null?new JSONObject():existing;
                obj.put("name",n); obj.put("phone",ph); obj.put("amount",val); obj.put("mode",cash.isChecked()?"Cash":"Online");
                obj.put("date",date.getText().toString().trim()); obj.put("note",note.getText().toString().trim());
                if(!obj.has("createdAt")) obj.put("createdAt",System.currentTimeMillis());
                if(editIndex>=0) all.put(editIndex,obj); else all.put(obj); saveArray(VARGANI,all);
                showSuccessDialog(editIndex>=0?tr("Vargani updated","वर्गणी अपडेट झाली"):tr("Vargani saved","वर्गणी सेव्ह झाली"),n+"  •  "+money(val),()->showContributors());
            } catch(Exception ex){showErrorDialog(tr("Could not save","सेव्ह करता आले नाही"),ex.getMessage());}
        });
        root.addView(scroll,lp(-1,0,1));
    }

    private String marathiMessage(String name, double amount) {
        return "नमस्कार " + name + "जी 🙏\n\n" +
                "आपली ₹" + formatAmount(amount) + " वर्गणी छावा प्रतिष्ठानसाठी प्राप्त झाली आहे.\n\n" +
                "आपल्या सहकार्याबद्दल मनःपूर्वक धन्यवाद! ❤️\n" +
                "आपले सहकार्य आमच्यासाठी खूप मोलाचे आहे.\n\n" +
                "🚩 जय भवानी! जय शिवराय! 🚩";
    }

    private void openWhatsApp(String phone, String message) {
        String d = digits(phone);
        if (d.length() == 10) d = "91" + d;
        String url = "https://wa.me/" + d + "?text=" + Uri.encode(message);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage("com.whatsapp");
        try {
            startActivity(intent);
            Toast.makeText(this, tr("WhatsApp opened with the message ready. Tap Send.", "WhatsApp मध्ये संदेश तयार आहे. Send दाबा."), Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Intent fallback = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            try { startActivity(fallback); }
            catch (Exception e) { showErrorDialog(tr("WhatsApp could not be opened", "WhatsApp उघडता आले नाही"), tr("Please check that WhatsApp is installed.", "कृपया WhatsApp इन्स्टॉल आहे का ते तपासा.")); }
        }
    }

    private void openSms(String phone, String message) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + digits(phone)));
            intent.putExtra("sms_body", message);
            startActivity(intent);
            Toast.makeText(this, tr("SMS opened with the message ready. Tap Send.", "SMS मध्ये संदेश तयार आहे. Send दाबा."), Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            showErrorDialog(tr("SMS could not be opened", "SMS उघडता आला नाही"), ex.getMessage());
        }
    }

    private void showAddExpense() { showExpenseForm(-1); }

    private void showExpenseForm(int editIndex) {
        currentScreen="expenseForm"; createRoot();
        root.addView(topBar(tr(editIndex>=0?"Edit Expense":"Add Expense",editIndex>=0?"खर्च संपादित करा":"खर्च नोंदवा"),true,null,""),lp(-1,dp(68)));
        ScrollView scroll=new ScrollView(this); scroll.setBackgroundColor(BG); LinearLayout c=col(); c.setPadding(dp(16),dp(14),dp(16),dp(24)); scroll.addView(c);
        JSONArray all=getArray(EXPENSES); JSONObject existing=editIndex>=0&&editIndex<all.length()?all.optJSONObject(editIndex):null;
        c.addView(sectionTitle(tr("Expense Details","खर्चाची माहिती")));
        EditText name=input(tr("Expense name * (e.g. decoration, food, sound)","खर्चाचे नाव * (उदा. सजावट, जेवण, साऊंड)"),InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        EditText amount=input(tr("Amount *","रक्कम *"),InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        EditText date=input(tr("Date (optional)","दिनांक (ऐच्छिक)"),InputType.TYPE_CLASS_TEXT); date.setFocusable(false);
        EditText note=input(tr("Note (optional)","नोंद (ऐच्छिक)"),InputType.TYPE_CLASS_TEXT);
        c.addView(name,lp(-1,dp(54)));addSpacer(c,10);c.addView(amount,lp(-1,dp(54)));addSpacer(c,10);c.addView(date,lp(-1,dp(54)));date.setOnClickListener(v->pickDate(date));addSpacer(c,10);c.addView(note,lp(-1,dp(54)));addSpacer(c,18);
        if(existing!=null){name.setText(existing.optString("name"));amount.setText(formatAmount(existing.optDouble("amount",0)));date.setText(existing.optString("date"));note.setText(existing.optString("note"));}
        Button save=actionButton("✓  "+tr(editIndex>=0?"Update Expense":"Save Expense",editIndex>=0?"खर्च अपडेट करा":"खर्च सेव्ह करा"),ORANGE,WHITE);c.addView(save,lp(-1,dp(56)));
        save.setOnClickListener(v->{String n=name.getText().toString().trim();double val=parse(amount.getText().toString().trim());if(n.isEmpty()){error(name,tr("Enter expense name","खर्चाचे नाव टाका"));return;}if(val<=0){error(amount,tr("Enter a valid amount","योग्य रक्कम टाका"));return;}try{JSONObject o=existing==null?new JSONObject():existing;o.put("name",n);o.put("amount",val);o.put("date",date.getText().toString().trim());o.put("note",note.getText().toString().trim());if(!o.has("createdAt"))o.put("createdAt",System.currentTimeMillis());if(editIndex>=0)all.put(editIndex,o);else all.put(o);saveArray(EXPENSES,all);showSuccessDialog(editIndex>=0?tr("Expense updated","खर्च अपडेट झाला"):tr("Expense saved","खर्च सेव्ह झाला"),n+"  •  "+money(val),()->showExpenses());}catch(Exception ex){showErrorDialog(tr("Could not save","सेव्ह करता आले नाही"),ex.getMessage());}});
        root.addView(scroll,lp(-1,0,1));
    }

    private void showContributors() {
        currentScreen="contributors"; createRoot(); root.addView(topBar(tr("Contributors","वर्गणीदार"),true,null,""),lp(-1,dp(68)));
        LinearLayout c=col();c.setBackgroundColor(BG);c.setPadding(dp(16),dp(12),dp(16),dp(20));
        EditText search=input(tr("Search by name or mobile","नाव किंवा मोबाईलने शोधा"),InputType.TYPE_CLASS_TEXT);c.addView(search,lp(-1,dp(52)));addSpacer(c,10);
        ScrollView scroll=new ScrollView(this);LinearLayout list=col();scroll.addView(list);c.addView(scroll,lp(-1,0,1));root.addView(c,lp(-1,0,1));
        Runnable render=()->{list.removeAllViews();String q=search.getText().toString().trim().toLowerCase(Locale.ROOT);JSONArray a=getArray(VARGANI);ArrayList<Integer> ids=new ArrayList<>();for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o==null)continue;String n=o.optString("name"),p=o.optString("phone");if(q.isEmpty()||n.toLowerCase(Locale.ROOT).contains(q)||p.contains(q))ids.add(i);}Collections.reverse(ids);if(ids.isEmpty()){empty(list,tr("No contributions found.","वर्गणीची नोंद सापडली नाही."));return;}for(int idx:ids){JSONObject o=a.optJSONObject(idx);if(o==null)continue;LinearLayout card=col();card.setPadding(dp(14),dp(12),dp(10),dp(10));card.setBackground(strokeBg(WHITE,BORDER,16));LinearLayout head=row();LinearLayout info=col();info.addView(tv("👤  "+o.optString("name"),16,TEXT,true));info.addView(tv(o.optString("phone"),12,MUTED,false));head.addView(info,lp(0,dp(48),1));head.addView(tv(money(o.optDouble("amount",0)),17,GREEN,true),lp(dp(100),dp(48)));card.addView(head);String meta=o.optString("mode");if(!o.optString("date").isEmpty())meta+=(meta.isEmpty()?"":"  •  ")+o.optString("date");if(!o.optString("note").isEmpty())meta+=(meta.isEmpty()?"":"  •  ")+o.optString("note");card.addView(tv(meta,12,MUTED,false));LinearLayout actions=row();actions.setGravity(Gravity.END);Button edit=actionButton("✎  "+tr("Edit","संपादित"),WHITE,TEXT);Button del=actionButton("🗑  "+tr("Delete","हटवा"),WHITE,RED);actions.addView(edit,lp(dp(100),dp(44)));margin(edit,0,8,6,0);actions.addView(del,lp(dp(100),dp(44)));margin(del,0,8,0,0);card.addView(actions);list.addView(card,lp(-1,dp(145)));margin(card,0,0,0,9);edit.setOnClickListener(v->showVarganiForm(idx));del.setOnClickListener(v->confirmDeleteVargani(idx));}}
        ;search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int b,int c){render.run();}public void afterTextChanged(Editable e){}});render.run();
    }

    private void showContributorDetails(String name) { showContributors(); }

    private void confirmDeleteVargani(int index){JSONArray a=getArray(VARGANI);JSONObject o=index>=0&&index<a.length()?a.optJSONObject(index):null;if(o==null)return;String title=o.optString("name")+"  •  "+money(o.optDouble("amount",0));new AlertDialog.Builder(this).setTitle(tr("Delete this contribution?","ही वर्गणीची नोंद हटवायची का?")).setMessage(title).setNegativeButton(tr("Cancel","रद्द"),null).setPositiveButton(tr("Delete","हटवा"),(d,w)->{JSONArray fresh=getArray(VARGANI);if(index>=0&&index<fresh.length()){fresh.remove(index);saveArray(VARGANI,fresh);Toast.makeText(this,tr("Contribution deleted","वर्गणीची नोंद हटवली"),Toast.LENGTH_SHORT).show();showContributors();}}).show();}

    private void showExpenses() {
        currentScreen="expenses";createRoot();root.addView(topBar(tr("Expenses","खर्च"),true,null,""),lp(-1,dp(68)));ScrollView scroll=new ScrollView(this);scroll.setBackgroundColor(BG);LinearLayout list=col();list.setPadding(dp(16),dp(14),dp(16),dp(24));scroll.addView(list);root.addView(scroll,lp(-1,0,1));
        JSONArray a=getArray(EXPENSES);ArrayList<Integer> ids=new ArrayList<>();for(int i=0;i<a.length();i++)if(a.optJSONObject(i)!=null)ids.add(i);Collections.reverse(ids);if(ids.isEmpty()){empty(list,tr("No expenses recorded yet.","अजून कोणताही खर्च नोंदलेला नाही."));return;}
        for(int idx:ids){JSONObject o=a.optJSONObject(idx);LinearLayout card=col();card.setPadding(dp(14),dp(12),dp(10),dp(10));card.setBackground(strokeBg(WHITE,BORDER,16));card.addView(tv("🧾  "+o.optString("name"),16,TEXT,true));card.addView(tv("₹"+formatAmount(o.optDouble("amount",0)),19,RED,true));String meta=o.optString("date");if(!o.optString("note").isEmpty())meta+=(meta.isEmpty()?"":"  •  ")+o.optString("note");card.addView(tv(meta,12,MUTED,false));LinearLayout actions=row();actions.setGravity(Gravity.END);Button edit=actionButton("✎  "+tr("Edit","संपादित"),WHITE,TEXT);Button del=actionButton("🗑  "+tr("Delete","हटवा"),WHITE,RED);actions.addView(edit,lp(dp(100),dp(44)));margin(edit,0,8,6,0);actions.addView(del,lp(dp(100),dp(44)));margin(del,0,8,0,0);card.addView(actions);list.addView(card,lp(-1,dp(145)));margin(card,0,0,0,9);edit.setOnClickListener(v->showExpenseForm(idx));del.setOnClickListener(v->confirmDeleteExpense(idx));}
    }

    private void confirmDeleteExpense(int index){JSONArray a=getArray(EXPENSES);JSONObject o=index>=0&&index<a.length()?a.optJSONObject(index):null;if(o==null)return;new AlertDialog.Builder(this).setTitle(tr("Delete this expense?","हा खर्च हटवायचा का?")).setMessage(o.optString("name")+"  •  "+money(o.optDouble("amount",0))).setNegativeButton(tr("Cancel","रद्द"),null).setPositiveButton(tr("Delete","हटवा"),(d,w)->{JSONArray fresh=getArray(EXPENSES);if(index>=0&&index<fresh.length()){fresh.remove(index);saveArray(EXPENSES,fresh);Toast.makeText(this,tr("Expense deleted","खर्चाची नोंद हटवली"),Toast.LENGTH_SHORT).show();showExpenses();}}).show();}

    private void showReports() {
        currentScreen="reports"; createRoot(); root.addView(topBar(tr("Reports & Account","अहवाल आणि हिशोब"),true,null,""),lp(-1,dp(68)));
        ScrollView scroll=new ScrollView(this); scroll.setBackgroundColor(BG); LinearLayout c=col();c.setPadding(dp(16),dp(14),dp(16),dp(30));scroll.addView(c);root.addView(scroll,lp(-1,0,1));
        double total=totalVargani(), exp=totalExpenses(), bal=total-exp, cash=modeTotal("Cash"), online=modeTotal("Online");
        c.addView(sectionTitle(tr("Account Summary","हिशोबाचा सारांश")));
        c.addView(reportLine(tr("Total Vargani","एकूण वर्गणी"),money(total),GREEN));
        c.addView(reportLine(tr("Cash","रोख"),money(cash),TEXT));
        c.addView(reportLine(tr("Online","ऑनलाइन"),money(online),TEXT));
        c.addView(reportLine(tr("Total Expenses","एकूण खर्च"),money(exp),RED));
        c.addView(reportLine(tr("Remaining Balance","शिल्लक"),money(bal),DARK));
        addSpacer(c,16); c.addView(sectionTitle(tr("Top Contributors","जास्त वर्गणी देणारे")));
        HashMap<String,Double> map=new HashMap<>(); JSONArray va=getArray(VARGANI); for(int i=0;i<va.length();i++){JSONObject o=va.optJSONObject(i);if(o!=null){String n=o.optString("name");map.put(n,map.getOrDefault(n,0.0)+o.optDouble("amount",0));}}
        ArrayList<Map.Entry<String,Double>> entries=new ArrayList<>(map.entrySet()); entries.sort((a,b)->Double.compare(b.getValue(),a.getValue()));
        if(entries.isEmpty()) empty(c,tr("No contributor data yet.","अजून वर्गणीदारांची माहिती नाही."));
        else {int limit=Math.min(10,entries.size());for(int i=0;i<limit;i++){Map.Entry<String,Double> e=entries.get(i);LinearLayout r=row();TextView n=tv((i+1)+". "+e.getKey(),14,TEXT,true);TextView v=tv(money(e.getValue()),15,GREEN,true);r.addView(n,lp(0,dp(44),1));r.addView(v,lp(dp(110),dp(44)));r.setPadding(dp(12),0,dp(12),0);r.setBackground(strokeBg(WHITE,BORDER,10));c.addView(r,lp(-1,dp(44)));margin(r,0,0,0,7);}}
        addSpacer(c,14); c.addView(sectionTitle(tr("Data Management","डेटा व्यवस्थापन")));
        Button pdf=actionButton("▣  "+tr("Generate PDF Report","PDF अहवाल तयार करा"),GREEN,WHITE);c.addView(pdf,lp(-1,dp(54)));margin(pdf,0,0,0,10);pdf.setOnClickListener(v->generatePdfReport());
        Button clear=actionButton("⚠  "+tr("Clear all saved data","सर्व सेव्ह केलेला डेटा हटवा"),WHITE,RED);c.addView(clear,lp(-1,dp(52)));clear.setOnClickListener(v->confirmClear());
    }

    private TextView reportLine(String label,String value,int valueColor){TextView t=tv(label+"    "+value,15,TEXT,true);t.setPadding(dp(14),0,dp(14),0);t.setMinHeight(dp(52));t.setBackground(strokeBg(WHITE,BORDER,12));t.setTextColor(TEXT);return t;}

    private void confirmClear(){new AlertDialog.Builder(this).setTitle(tr("Delete all data?","सर्व डेटा हटवायचा का?" )).setMessage(tr("All saved contributions and expenses will be permanently removed from this phone.","या फोनमधील सर्व वर्गणी आणि खर्च कायमचे हटवले जातील." )).setNegativeButton(tr("Cancel","रद्द"),null).setPositiveButton(tr("Delete","हटवा"),(d,w)->{prefs.edit().remove(VARGANI).remove(EXPENSES).apply();Toast.makeText(this,tr("All data cleared","सर्व डेटा हटवला"),Toast.LENGTH_SHORT).show();showHome();}).show();}

    private byte[] pendingPdfBytes;
    private static final int CREATE_PDF = 9021;

    private void generatePdfReport(){
        try{
            android.graphics.pdf.PdfDocument doc=new android.graphics.pdf.PdfDocument();
            android.graphics.Paint paint=new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);paint.setTypeface(Typeface.DEFAULT);paint.setTextSize(11);paint.setColor(Color.BLACK);
            android.graphics.Paint bold=new android.graphics.Paint(paint);bold.setTypeface(Typeface.DEFAULT_BOLD);bold.setTextSize(13);
            int pageNo=1;android.graphics.pdf.PdfDocument.Page page=doc.startPage(new android.graphics.pdf.PdfDocument.PageInfo.Builder(595,842,pageNo).create());android.graphics.Canvas canvas=page.getCanvas();float y=42;
            canvas.drawText("। छावा प्रतिष्ठान ।",40,y,bold);y+=24;canvas.drawText("Vargani Manager - Complete Report",40,y,bold);y+=20;canvas.drawText("Generated: "+new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.getDefault()).format(new Date()),40,y,paint);y+=28;
            double total=totalVargani(),exp=totalExpenses(),cash=modeTotal("Cash"),online=modeTotal("Online");
            canvas.drawText("Total Vargani: "+money(total),40,y,bold);canvas.drawText("Cash: "+money(cash),210,y,paint);canvas.drawText("Online: "+money(online),330,y,paint);y+=20;canvas.drawText("Expenses: "+money(exp),40,y,paint);canvas.drawText("Balance: "+money(total-exp),210,y,paint);y+=30;
            canvas.drawText("CONTRIBUTIONS",40,y,bold);y+=20;canvas.drawText("Name",40,y,bold);canvas.drawText("Mobile",210,y,bold);canvas.drawText("Amount",360,y,bold);canvas.drawText("Mode",440,y,bold);y+=16;
            JSONArray va=getArray(VARGANI);for(int i=0;i<va.length();i++){JSONObject o=va.optJSONObject(i);if(o==null)continue;if(y>800){doc.finishPage(page);pageNo++;page=doc.startPage(new android.graphics.pdf.PdfDocument.PageInfo.Builder(595,842,pageNo).create());canvas=page.getCanvas();y=42;}canvas.drawText(o.optString("name"),40,y,paint);canvas.drawText(o.optString("phone"),210,y,paint);canvas.drawText(money(o.optDouble("amount",0)),360,y,paint);canvas.drawText(o.optString("mode"),440,y,paint);y+=16;}
            y+=14;if(y>780){doc.finishPage(page);pageNo++;page=doc.startPage(new android.graphics.pdf.PdfDocument.PageInfo.Builder(595,842,pageNo).create());canvas=page.getCanvas();y=42;}canvas.drawText("EXPENSES",40,y,bold);y+=20;canvas.drawText("Expense",40,y,bold);canvas.drawText("Amount",360,y,bold);canvas.drawText("Date",440,y,bold);y+=16;
            JSONArray ea=getArray(EXPENSES);for(int i=0;i<ea.length();i++){JSONObject o=ea.optJSONObject(i);if(o==null)continue;if(y>800){doc.finishPage(page);pageNo++;page=doc.startPage(new android.graphics.pdf.PdfDocument.PageInfo.Builder(595,842,pageNo).create());canvas=page.getCanvas();y=42;}canvas.drawText(o.optString("name"),40,y,paint);canvas.drawText(money(o.optDouble("amount",0)),360,y,paint);canvas.drawText(o.optString("date"),440,y,paint);y+=16;}
            doc.finishPage(page);java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream();doc.writeTo(out);doc.close();pendingPdfBytes=out.toByteArray();Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT);intent.setType("application/pdf");intent.putExtra(Intent.EXTRA_TITLE,"Chhava_Pratishthan_Vargani_Report.pdf");startActivityForResult(intent,CREATE_PDF);
        }catch(Exception ex){showErrorDialog(tr("PDF could not be generated","PDF तयार करता आला नाही"),ex.getMessage());}
    }

    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){super.onActivityResult(requestCode,resultCode,data);if(requestCode==CREATE_PDF&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null&&pendingPdfBytes!=null){try(java.io.OutputStream os=getContentResolver().openOutputStream(data.getData())){os.write(pendingPdfBytes);os.flush();pendingPdfBytes=null;Toast.makeText(this,tr("PDF saved successfully","PDF यशस्वीपणे सेव्ह झाला"),Toast.LENGTH_LONG).show();}catch(Exception ex){showErrorDialog(tr("Could not save PDF","PDF सेव्ह करता आला नाही"),ex.getMessage());}}}

    private void pickDate(EditText target){final java.util.Calendar cal=java.util.Calendar.getInstance();DatePickerDialog d=new DatePickerDialog(this,(v,y,m,day)->{target.setText(String.format(Locale.getDefault(),"%02d/%02d/%04d",day,m+1,y));},cal.get(java.util.Calendar.YEAR),cal.get(java.util.Calendar.MONTH),cal.get(java.util.Calendar.DAY_OF_MONTH));d.show();}

    private void empty(LinearLayout list,String text){TextView e=tv(text,15,MUTED,true);e.setGravity(Gravity.CENTER);e.setPadding(dp(10),dp(30),dp(10),dp(30));list.addView(e,lp(-1,dp(110)));}
    private void error(EditText field,String message){field.setError(message);field.requestFocus();}
    private void showSuccessDialog(String title, String message, final Runnable after) {
        new AlertDialog.Builder(this)
                .setTitle("✓  " + title)
                .setMessage(message)
                .setPositiveButton(tr("OK", "ठीक"), (d, w) -> {
                    if (after != null) after.run();
                })
                .show();
    }

    private void showErrorDialog(String title,String message){new AlertDialog.Builder(this).setTitle(title).setMessage(message==null?"":message).setPositiveButton(tr("OK","ठीक"),null).show();}

    private JSONArray getArray(String key){try{return new JSONArray(prefs.getString(key,"[]"));}catch(Exception e){return new JSONArray();}}
    private void saveArray(String key,JSONArray a){prefs.edit().putString(key,a.toString()).apply();}
    private double totalVargani(){JSONArray a=getArray(VARGANI);double t=0;for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o!=null)t+=o.optDouble("amount",0);}return t;}
    private double modeTotal(String mode){JSONArray a=getArray(VARGANI);double t=0;for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o!=null&&mode.equalsIgnoreCase(o.optString("mode")))t+=o.optDouble("amount",0);}return t;}
    private double totalExpenses(){JSONArray a=getArray(EXPENSES);double t=0;for(int i=0;i<a.length();i++){JSONObject o=a.optJSONObject(i);if(o!=null)t+=o.optDouble("amount",0);}return t;}
    private double parse(String s){try{return Double.parseDouble(s.replace(",",""));}catch(Exception e){return 0;}}
    private String digits(String s){return s==null?"":s.replaceAll("[^0-9]","");}
    private String formatAmount(double x){if(Math.rint(x)==x)return String.format(Locale.getDefault(),"%.0f",x);return String.format(Locale.getDefault(),"%.2f",x);}
    private String money(double x){return "₹"+formatAmount(x);}

    @Override public void onBackPressed(){if("home".equals(currentScreen)){super.onBackPressed();}else showHome();}
}
