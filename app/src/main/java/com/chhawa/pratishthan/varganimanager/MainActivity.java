package com.chhawa.pratishthan.varganimanager;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    static final String PREF = "vargani_data";
    static final String CONTRIBUTIONS = "contributions";
    static final String EXPENSES = "expenses";
    SharedPreferences sp;
    LinearLayout root, content;
    boolean marathi = false;
    final int BG = Color.rgb(250,245,238), DARK = Color.rgb(45,32,26), GREEN = Color.rgb(20,138,82), ORANGE = Color.rgb(243,107,33);

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        sp = getSharedPreferences(PREF, MODE_PRIVATE);
        marathi = sp.getBoolean("marathi", false);
        showHome();
    }

    TextView tv(String s, float size, int color) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        t.setPadding(4,4,4,4); return t;
    }
    Button btn(String s, int color) {
        Button b = new Button(this); b.setText(s); b.setTextSize(15); b.setTextColor(Color.WHITE);
        b.setAllCaps(false); b.setBackgroundResource(color==GREEN?R.drawable.bg_green_button:R.drawable.bg_orange_button);
        return b;
    }
    LinearLayout box() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(14,14,14,14); l.setBackgroundResource(R.drawable.bg_card);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,-2); p.setMargins(8,8,8,8); l.setLayoutParams(p);
        return l;
    }
    EditText edit(String hint, boolean number) {
        EditText e = new EditText(this); e.setHint(hint); e.setTextSize(16); e.setSingleLine(true);
        if(number) e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        e.setPadding(12,10,12,10); return e;
    }
    void base(String title) {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(BG);
        LinearLayout head = new LinearLayout(this); head.setGravity(Gravity.CENTER_VERTICAL); head.setPadding(14,8,14,8);
        head.setBackgroundColor(DARK);
        TextView h = tv(title,20,Color.WHITE); h.setTypeface(null,1);
        head.addView(h,new LinearLayout.LayoutParams(0,60,1));
        TextView lang = tv(marathi?"EN":"मराठी",14,Color.WHITE); lang.setGravity(Gravity.CENTER);
        lang.setOnClickListener(v->{ marathi=!marathi; sp.edit().putBoolean("marathi",marathi).apply(); showHome();});
        head.addView(lang,new LinearLayout.LayoutParams(70,60));
        root.addView(head);
        content = new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(10,10,10,20);
        ScrollView sv = new ScrollView(this); sv.addView(content); root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
    }
    String tr(String en, String mr){return marathi?mr:en;}

    JSONArray arr(String key){ try{return new JSONArray(sp.getString(key,"[]"));}catch(Exception e){return new JSONArray();}}
    void save(String key, JSONArray a){sp.edit().putString(key,a.toString()).apply();}
    double total(String key){
        double x=0; JSONArray a=arr(key); for(int i=0;i<a.length();i++) try{x+=a.getDouble(i);}catch(Exception ignored){}
        return x;
    }
    String money(double x){return "₹ "+String.format(Locale.US,"%,.0f",x);}

    void showHome(){
        base("🚩 । छावा प्रतिष्ठान । 🚩");
        double c=total(CONTRIBUTIONS), e=total(EXPENSES), bal=c-e;
        TextView sub=tv(tr("Vargani Manager","वर्गणी व्यवस्थापक"),15,Color.DKGRAY); sub.setGravity(Gravity.CENTER);
        content.addView(sub);
        LinearLayout stats=new LinearLayout(this); stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.addView(stat(tr("Total Vargani","एकूण वर्गणी"),money(c),GREEN),new LinearLayout.LayoutParams(0,-2,1));
        stats.addView(stat(tr("Total Expenses","एकूण खर्च"),money(e),Color.rgb(210,55,55)),new LinearLayout.LayoutParams(0,-2,1));
        content.addView(stats);
        LinearLayout b=box(); TextView a=tv(tr("Remaining Balance","शिल्लक रक्कम"),15,DARK); a.setGravity(Gravity.CENTER);
        TextView bv=tv(money(bal),28,bal>=0?GREEN:Color.RED); bv.setGravity(Gravity.CENTER); bv.setTypeface(null,1); b.addView(a); b.addView(bv); content.addView(b);
        double cash=modeTotal("Cash"), online=modeTotal("Online");
        LinearLayout modes=new LinearLayout(this); modes.setOrientation(LinearLayout.HORIZONTAL);
        modes.addView(stat("💵 "+tr("Cash","रोख"),money(cash),GREEN),new LinearLayout.LayoutParams(0,-2,1));
        modes.addView(stat("📱 "+tr("Online","ऑनलाइन"),money(online),Color.rgb(45,110,220)),new LinearLayout.LayoutParams(0,-2,1));
        content.addView(modes);

        Button addC=btn("＋  "+tr("Add Vargani","वर्गणी जमा करा"),GREEN); addC.setOnClickListener(v->showAddContribution()); content.addView(addC);
        Button addE=btn("＋  "+tr("Add Expense","खर्च नोंदवा"),ORANGE); addE.setOnClickListener(v->showAddExpense()); content.addView(addE);
        LinearLayout grid=new LinearLayout(this); grid.setOrientation(LinearLayout.HORIZONTAL);
        Button cbtn=btn("👥 "+tr("Contributors","वर्गणीदार"),GREEN); cbtn.setOnClickListener(v->showContributors());
        Button ebtn=btn("💸 "+tr("Expenses","खर्च"),ORANGE); ebtn.setOnClickListener(v->showExpenses());
        grid.addView(cbtn,new LinearLayout.LayoutParams(0,70,1)); grid.addView(ebtn,new LinearLayout.LayoutParams(0,70,1)); content.addView(grid);
        Button report=btn("📊 "+tr("Reports","अहवाल"),GREEN); report.setOnClickListener(v->showReport()); content.addView(report);
    }
    LinearLayout stat(String title,String value,int color){
        LinearLayout l=box(); TextView t=tv(title,13,DARK); t.setGravity(Gravity.CENTER); TextView v=tv(value,20,color); v.setGravity(Gravity.CENTER); v.setTypeface(null,1); l.addView(t);l.addView(v);return l;
    }
    double modeTotal(String mode){
        double x=0; JSONArray a=arr(CONTRIBUTIONS); for(int i=0;i<a.length();i++) try{JSONObject o=a.getJSONObject(i);if(mode.equals(o.optString("mode")))x+=o.optDouble("amount");}catch(Exception ignored){}
        return x;
    }

    void showAddContribution(){
        base(tr("Add Vargani","वर्गणी जमा करा"));
        EditText name=edit(tr("Full name / पूर्ण नाव","पूर्ण नाव"),false), phone=edit(tr("Mobile number / मोबाईल नंबर","मोबाईल नंबर"),false), amt=edit(tr("Amount ₹","रक्कम ₹"),true), note=edit(tr("Note (optional)","नोंद (ऐच्छिक)"),false);
        content.addView(label(tr("Name *","नाव *")));content.addView(name);content.addView(label(tr("Mobile number","मोबाईल नंबर")));content.addView(phone);content.addView(label(tr("Amount *","रक्कम *")));content.addView(amt);
        Spinner mode=new Spinner(this); mode.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"Cash / रोख","Online / ऑनलाइन"})); content.addView(mode);
        content.addView(label(tr("Note","नोंद")));content.addView(note);
        Button save=btn("✓  "+tr("Save & Choose Message","सेव्ह करा व संदेश निवडा"),GREEN);content.addView(save);
        save.setOnClickListener(v->{
            if(name.getText().toString().trim().isEmpty()||amt.getText().toString().trim().isEmpty()){toast(tr("Name and amount are required","नाव आणि रक्कम आवश्यक आहे"));return;}
            try{
                double val=Double.parseDouble(amt.getText().toString()); JSONObject o=new JSONObject();
                o.put("name",name.getText().toString().trim());o.put("phone",phone.getText().toString().trim());o.put("amount",val);
                o.put("mode",mode.getSelectedItemPosition()==0?"Cash":"Online");o.put("note",note.getText().toString());o.put("date",today());
                JSONArray a=arr(CONTRIBUTIONS);a.put(o);save(CONTRIBUTIONS,a);showMessageChoice(o);
            }catch(Exception ex){toast("Could not save.");}
        });
    }
    TextView label(String s){TextView t=tv(s,13,DARK);t.setPadding(4,12,4,4);return t;}
    String today(){return new SimpleDateFormat("dd MMM yyyy",Locale.getDefault()).format(new Date());}

    void showMessageChoice(JSONObject o){
        base(tr("Send Message","संदेश पाठवा"));
        TextView info=tv(tr("Contribution saved. Choose how to thank the contributor.","वर्गणी जतन झाली. धन्यवाद संदेश पाठवण्याचा पर्याय निवडा."),16,DARK);content.addView(info);
        String msg=tr("🙏 Jai Shree Ganesh 🙏\n\nDear ","🙏 जय श्री गणेश 🙏\n\nप्रिय ")+o.optString("name")+",\n\n"+
                tr("Thank you for contributing ","तुम्ही ")+money(o.optDouble("amount"))+
                tr(" towards 🚩 । छावा प्रतिष्ठान । 🚩.\n\nYour contribution has been received successfully.\n\nGanpati Bappa Morya!"," 🚩 । छावा प्रतिष्ठान । 🚩 साठी दिल्याबद्दल धन्यवाद.\n\nतुमची वर्गणी यशस्वीरित्या नोंदवली आहे.\n\nगणपती बाप्पा मोरया!");
        TextView preview=tv(msg,15,DARK);preview.setPadding(16,16,16,16);preview.setBackgroundResource(R.drawable.bg_card);content.addView(preview);
        Button wa=btn("🟢  "+tr("Send on WhatsApp","WhatsApp वर पाठवा"),GREEN);content.addView(wa);
        Button sms=btn("✉  "+tr("Send by SMS","SMS ने पाठवा"),ORANGE);content.addView(sms);
        wa.setOnClickListener(v->sendWhatsApp(o.optString("phone"),msg));
        sms.setOnClickListener(v->sendSms(o.optString("phone"),msg));
        Button done=btn(tr("Done","पूर्ण"),GREEN);content.addView(done);done.setOnClickListener(v->showHome());
    }
    void sendWhatsApp(String phone,String msg){
        if(phone.trim().isEmpty()){toast(tr("No mobile number was entered.","मोबाईल नंबर दिलेला नाही."));return;}
        try{Intent i=new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+phone));i.setPackage("com.whatsapp");i.putExtra("sms_body",msg);startActivity(i);}
        catch(Exception e){toast(tr("WhatsApp is not available. Try SMS.","WhatsApp उपलब्ध नाही. SMS वापरा."));}
    }
    void sendSms(String phone,String msg){
        if(phone.trim().isEmpty()){toast(tr("No mobile number was entered.","मोबाईल नंबर दिलेला नाही."));return;}
        Intent i=new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+phone));i.putExtra("sms_body",msg);startActivity(i);
    }

    void showAddExpense(){
        base(tr("Add Expense","खर्च नोंदवा"));
        EditText name=edit(tr("Expense name, e.g. Decoration","खर्चाचे नाव, उदा. सजावट"),false), amt=edit(tr("Amount ₹","रक्कम ₹"),true), note=edit(tr("Note (optional)","नोंद (ऐच्छिक)"),false);
        content.addView(label(tr("Expense name *","खर्चाचे नाव *")));content.addView(name);content.addView(label(tr("Amount *","रक्कम *")));content.addView(amt);content.addView(label(tr("Payment mode","पेमेंट पद्धत")));
        Spinner mode=new Spinner(this);mode.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"Cash / रोख","Online / ऑनलाइन"}));content.addView(mode);content.addView(label(tr("Note","नोंद")));content.addView(note);
        Button save=btn("✓  "+tr("Save Expense","खर्च सेव्ह करा"),ORANGE);content.addView(save);
        save.setOnClickListener(v->{if(name.getText().toString().trim().isEmpty()||amt.getText().toString().trim().isEmpty()){toast(tr("Expense name and amount are required","खर्चाचे नाव आणि रक्कम आवश्यक आहे"));return;}try{JSONObject o=new JSONObject();o.put("name",name.getText().toString().trim());o.put("amount",Double.parseDouble(amt.getText().toString()));o.put("mode",mode.getSelectedItemPosition()==0?"Cash":"Online");o.put("note",note.getText().toString());o.put("date",today());JSONArray a=arr(EXPENSES);a.put(o);save(EXPENSES,a);showHome();}catch(Exception ignored){toast("Could not save.");}});
    }

    void showContributors(){
        base(tr("Contributors","वर्गणीदार"));
        JSONArray a=arr(CONTRIBUTIONS);
        if(a.length()==0){content.addView(tv(tr("No contributors yet.","अजून वर्गणीदार नाहीत."),16,DARK));return;}
        for(int i=a.length()-1;i>=0;i--)try{JSONObject o=a.getJSONObject(i);LinearLayout b=box();b.addView(tv(o.optString("name"),17,DARK));b.addView(tv(money(o.optDouble("amount"))+" • "+o.optString("mode")+" • "+o.optString("date"),13,Color.DKGRAY));content.addView(b);}catch(Exception ignored){}
    }
    void showExpenses(){
        base(tr("Expenses","खर्च"));
        JSONArray a=arr(EXPENSES);
        if(a.length()==0){content.addView(tv(tr("No expenses yet.","अजून खर्च नाही."),16,DARK));return;}
        for(int i=a.length()-1;i>=0;i--)try{JSONObject o=a.getJSONObject(i);LinearLayout b=box();b.addView(tv(o.optString("name"),17,DARK));b.addView(tv(money(o.optDouble("amount"))+" • "+o.optString("mode")+" • "+o.optString("date"),13,Color.DKGRAY));if(!o.optString("note").isEmpty())b.addView(tv(o.optString("note"),13,Color.GRAY));content.addView(b);}catch(Exception ignored){}
    }
    void showReport(){
        base(tr("Reports / Summary","अहवाल / सारांश"));
        double c=total(CONTRIBUTIONS),e=total(EXPENSES);
        String[] lines={tr("Total Collection","एकूण वर्गणी")+": "+money(c),tr("Total Expenses","एकूण खर्च")+": "+money(e),tr("Remaining Balance","शिल्लक रक्कम")+": "+money(c-e),tr("Cash Collection","रोख वर्गणी")+": "+money(modeTotal("Cash")),tr("Online Collection","ऑनलाइन वर्गणी")+": "+money(modeTotal("Online"))};
        for(String s:lines){LinearLayout b=box();b.addView(tv(s,18,DARK));content.addView(b);}
        Button clear=btn(tr("Reset all data","सर्व डेटा रीसेट करा"),ORANGE);content.addView(clear);clear.setOnClickListener(v->new AlertDialog.Builder(this).setTitle(tr("Reset data?","डेटा रीसेट करायचा?")).setMessage(tr("This cannot be undone.","ही कृती पूर्ववत करता येणार नाही.")).setPositiveButton(tr("Reset","रीसेट"),(d,w)->{sp.edit().clear().apply();showHome();}).setNegativeButton(tr("Cancel","रद्द"),null).show());
    }
    void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
}
