package me.tasy5kg.cutechem;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.googlecode.aviator.AviatorEvaluator;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements TextWatcher, View.OnClickListener {
    private static final HashMap<String, Double> ELEMENT_TO_MASS_HASH_MAP
            = new HashMap<>();
    private static final HashMap<String, String> CHAR_TO_SUBSCRIPT_HASH_MAP
            = new HashMap<>();
    private static final HashMap<String, String> SUBSCRIPT_TO_CHAR_HASH_MAP
            = new HashMap<>();
    private static final HashMap<String, String> CHAR_TO_SUPERSCRIPT_HASH_MAP
            = new HashMap<>();
    private static final String[] ELEMENTS = {
            "Ac", "Ag", "Al", "Am", "Ar", "As", "At", "Au", "Ba", "Be", "Bh",
            "Bi", "Bk", "Br", "Ca", "Cd", "Ce", "Cf", "Cl", "Cm", "Cn", "Co",
            "Cr", "Cs", "Cu", "Db", "Ds", "Dy", "Er", "Es", "Eu", "Fe", "Fl",
            "Fm", "Fr", "Ga", "Gd", "Ge", "He", "Hf", "Hg", "Ho", "Hs", "In",
            "Ir", "Kr", "La", "Li", "Lr", "Lu", "Lv", "Mc", "Md", "Mg", "Mn",
            "Mo", "Mt", "Na", "Nb", "Nd", "Ne", "Nh", "Ni", "No", "Np", "Og",
            "Os", "Pa", "Pb", "Pd", "Pm", "Po", "Pr", "Pt", "Pu", "Ra", "Rb",
            "Re", "Rf", "Rg", "Rh", "Rn", "Ru", "Sb", "Sc", "Se", "Sg", "Si",
            "Sm", "Sn", "Sr", "Ta", "Tb", "Tc", "Te", "Th", "Ti", "Tl", "Tm",
            "Ts", "Xe", "Yb", "Zn", "Zr", "B", "C", "F", "H", "I", "K", "N",
            "O", "P", "S", "U", "V", "W", "Y"};
    private static final String UPPERCASE_LETTER = "QWERTYUIOPASDFGHJKLZXCVBNM";
    private static final String BRACKETS_LEFT = "{[(";
    private static final String BRACKETS_RIGHT = ")]}";
    private static final String NUMBERS = "0123456789";
    private static final String DOT = "·";
    private static final String PLUS_MINUS = "+-";
    private static final String NUMBER_1_TO_7 = "1234567";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor sharedPreferencesEditor;
    private static MenuItem menuCheckBoxHighPrecision;
    private static MaterialTextView massMaterialTextView;
    private static MaterialTextView chemicalFormulaMaterialTextView;
    private static TextInputEditText chemicalFormulaTextInputEditText;
    private static Chip[] chips;
    private static String chipText;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("cuteChemData", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        loadRelativeAtomicMassMap();
        loadCharToSubscriptMap();
        loadCharToSuperscriptMap();
        loadSubscriptToCharMap();
        setContentView(R.layout.activity_main);
        MaterialToolbar toolbar = findViewById(R.id.material_main_toolbar);
        setSupportActionBar(toolbar);
        loadChips();
        loadOnClickListener();
        massMaterialTextView = findViewById(R.id.mass_material_text_view);
        chemicalFormulaMaterialTextView = findViewById(R.id.chemical_formula_material_text_view);
        chemicalFormulaTextInputEditText = findViewById(R.id.chemical_formula_text_input_edit_text);
        chemicalFormulaTextInputEditText.addTextChangedListener(this);
        chemicalFormulaTextInputEditText.requestFocus();
        MaterialButton materialButtonCopyChemicalFormula
                = findViewById(R.id.material_button_copy_chemical_formula);
        MaterialButton materialButtonCopyRelativeMass
                = findViewById(R.id.material_button_copy_relative_mass);
        materialButtonCopyChemicalFormula.setOnClickListener(this);
        materialButtonCopyRelativeMass.setOnClickListener(this);
        chipText = sharedPreferences.getString("historyChemicalFormula"
                , "H2O,SO42-,NH41+,CuSO4·5H2O,[Fe(NO)(H2O)5]SO4");
        displayChipText();
        TextInputLayout chemicalFormulaTextInputLayout = findViewById(R.id.chemical_formula_text_input_layout);
        chemicalFormulaTextInputLayout.setEndIconOnClickListener(v -> {
            //noinspection ConstantConditions
            updateChips(chemicalFormulaTextInputEditText.getText().toString());
            chemicalFormulaTextInputEditText.setText("");
        });
    }

    private void displayChipText() {
        int i = 0;
        for (String s : chipText.split(",")) {
            chips[i].setText(s);
            i++;
        }
    }

    private void updateChips(String newChipText) {
        if (!("," + chipText + ",").contains("," + newChipText + ",")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(newChipText).append(",");
            for (int i = 0; i < 4; i++) {
                stringBuilder.append(chipText.split(",")[i]).append(",");
            }
            chipText = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
            displayChipText();
            sharedPreferencesEditor.putString("historyChemicalFormula", chipText);
            sharedPreferencesEditor.apply();
        }
    }

    private void loadChips() {
        chips = new Chip[]{findViewById(R.id.example_chip1),
                findViewById(R.id.example_chip2),
                findViewById(R.id.example_chip3),
                findViewById(R.id.example_chip4),
                findViewById(R.id.example_chip5)};
    }

    private void loadOnClickListener() {
        findViewById(R.id.quick_input_material_button1).setOnClickListener(this);
        findViewById(R.id.quick_input_material_button2).setOnClickListener(this);
        findViewById(R.id.quick_input_material_button3).setOnClickListener(this);
        findViewById(R.id.quick_input_material_button4).setOnClickListener(this);
        findViewById(R.id.quick_input_material_button5).setOnClickListener(this);
        findViewById(R.id.quick_input_material_button6).setOnClickListener(this);
        findViewById(R.id.quick_input_material_button7).setOnClickListener(this);
        findViewById(R.id.quick_input_material_button8).setOnClickListener(this);
        findViewById(R.id.quick_input_material_button9).setOnClickListener(this);
        for (Chip chip : chips) {
            chip.setOnClickListener(this);
        }
    }

    @NonNull
    private String optimizedChemicalFormula(@NonNull String chemicalFormula) {
        if (chemicalFormula.equals("")) {
            return "";
        }
        int i = 0;
        StringBuilder optimizedChemicalFormula = new StringBuilder();
        while (i < chemicalFormula.length()) {
            boolean hasPreviousChar = (i > 0);
            boolean hasNextChar = (i + 1 < chemicalFormula.length());
            String thisChar = chemicalFormula.charAt(i) + "";
            String previousChar = hasPreviousChar ?
                    chemicalFormula.charAt(i - 1) + "" : "";
            String nextChar = hasNextChar ?
                    chemicalFormula.charAt(i + 1) + "" : "";
            if (PLUS_MINUS.contains(thisChar)) {
                optimizedChemicalFormula.append(
                        CHAR_TO_SUPERSCRIPT_HASH_MAP.get(thisChar));
            } else if (NUMBER_1_TO_7.contains(thisChar)
                    && hasNextChar
                    && PLUS_MINUS.contains(nextChar)
            ) {
                optimizedChemicalFormula.append(thisChar.equals("1") ? "" :
                        CHAR_TO_SUPERSCRIPT_HASH_MAP.get(thisChar));
            } else if (NUMBERS.contains(thisChar)
                    && hasPreviousChar
                    && !DOT.equals(previousChar)
                    && !BRACKETS_LEFT.equals(previousChar)
                    && !NUMBERS.contains(optimizedChemicalFormula
                    .charAt(i - 1) + "")
            ) {
                optimizedChemicalFormula.append(CHAR_TO_SUBSCRIPT_HASH_MAP
                        .get(thisChar));
            } else {
                optimizedChemicalFormula.append(chemicalFormula.charAt(i));
            }
            i++;
        }
        return optimizedChemicalFormula.toString();
    }

    private void updateMass() {
        String chemicalFormula = chemicalFormulaMaterialTextView.getText()
                .toString();
        if (chemicalFormula.equals("") || !chemicalFormula
                .matches(".*[A-Z].*")) {
            massMaterialTextView.setText("-");
            return;
        }

        //  Unity all brackets and fill in missing right brackets
        //  Verify count of dot no more than 1
        //  Remove all superscript chars
        int bracketsLeftCount = 0;
        int bracketsRightCount = 0;
        int dotCount = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : chemicalFormula.split("")) {
            if (BRACKETS_LEFT.contains(s)) {
                stringBuilder.append("(");
                bracketsLeftCount++;
            } else if (BRACKETS_RIGHT.contains(s)) {
                stringBuilder.append(")");
                bracketsRightCount++;
            } else if (s.equals(DOT)) {
                stringBuilder.append("+");
                dotCount++;
            } else if (!CHAR_TO_SUPERSCRIPT_HASH_MAP.containsValue(s)) {
                stringBuilder.append(s);
            }
        }
        if (dotCount > 1) {
            massMaterialTextView.setText("-");
            return;
        }
        if (bracketsLeftCount < bracketsRightCount) {
            massMaterialTextView.setText("-");
            return;
        } else {
            while (bracketsLeftCount > bracketsRightCount) {
                stringBuilder.append(")");
                bracketsRightCount++;
            }
        }
        chemicalFormula = stringBuilder.toString();

        //  Format all normal numbers
        stringBuilder = new StringBuilder();
        StringBuilder valueBuffer = new StringBuilder();
        boolean recordingNumber = false;
        int recordedCount = 0;
        for (String s : chemicalFormula.split("")) {
            if (NUMBERS.contains(s)) {
                recordingNumber = true;
                valueBuffer.append(s);
            } else {
                if (recordingNumber) {
                    stringBuilder.append(valueBuffer).append("*(").append(s);
                    valueBuffer = new StringBuilder();
                    recordedCount++;
                    recordingNumber = false;
                } else {
                    stringBuilder.append(s);
                }
            }
        }
        if (stringBuilder.toString().endsWith("+")) {
            massMaterialTextView.setText("-");
            return;
        }
        while (recordedCount > 0) {
            stringBuilder.append(")");
            recordedCount--;
        }
        chemicalFormula = stringBuilder.toString();

        //  Format all subscript numbers
        valueBuffer = new StringBuilder();
        stringBuilder = new StringBuilder();
        for (String s : chemicalFormula.split("")) {
            if (CHAR_TO_SUBSCRIPT_HASH_MAP.containsValue(s)) {
                valueBuffer.append(SUBSCRIPT_TO_CHAR_HASH_MAP.get(s));
            } else {
                if (valueBuffer.toString().equals("")) {
                    stringBuilder.append(s);
                } else {
                    stringBuilder.append("*").append(valueBuffer).append(s);
                    valueBuffer = new StringBuilder();
                }
            }
        }
        if (!valueBuffer.toString().equals("")) {
            stringBuilder.append("*").append(valueBuffer);
        }
        chemicalFormula = stringBuilder.toString();

        // Add a plus before each element
        stringBuilder = new StringBuilder();
        String previousChar = "";
        for (String s : chemicalFormula.split("")) {
            if ((UPPERCASE_LETTER.contains(s) || s.equals("("))
                    && !(previousChar.equals("*") || previousChar.equals("(")
                    || previousChar.equals(""))) {
                stringBuilder.append("+");
            }
            stringBuilder.append(s);
            previousChar = s;
        }

        chemicalFormula = stringBuilder.toString();

        //  Replace elements with mass
        for (String s : ELEMENTS) {
            if (chemicalFormula.contains(s)) {
                @SuppressWarnings("ConstantConditions") double highPrecisionMass
                        = ELEMENT_TO_MASS_HASH_MAP.get(s);
                double d = menuCheckBoxHighPrecision.isChecked() ?
                        highPrecisionMass : Math.round(highPrecisionMass * 2d) / 2d;
                chemicalFormula = chemicalFormula.replace(s, String.valueOf(d));
            }
        }
        if (chemicalFormula.contains("++")) {
            chemicalFormula = chemicalFormula
                    .replace("++", "+");
        }
        if (chemicalFormula.contains("+()")) {
            chemicalFormula = chemicalFormula
                    .replace("+()", "");
        }
        if (chemicalFormula.contains("()")) {
            chemicalFormula = chemicalFormula
                    .replace("()", "");
        }
        if (chemicalFormula.equals("")
                || chemicalFormula.matches(".*[a-zA-z].*")) {
            massMaterialTextView.setText("-");
        } else {
            double result = (double) AviatorEvaluator.execute(chemicalFormula);
            if (menuCheckBoxHighPrecision.isChecked()) {
                massMaterialTextView.setText(String.valueOf(
                        Math.round(result * 10000d) / 10000d));
            } else {
                massMaterialTextView.setText(String.valueOf(result));
            }
        }
    }

    private void loadRelativeAtomicMassMap() {
        ELEMENT_TO_MASS_HASH_MAP.put("H", 1.00794);
        ELEMENT_TO_MASS_HASH_MAP.put("He", 4.002602);
        ELEMENT_TO_MASS_HASH_MAP.put("Li", 6.941);
        ELEMENT_TO_MASS_HASH_MAP.put("Be", 9.0121831);
        ELEMENT_TO_MASS_HASH_MAP.put("B", 10.811);
        ELEMENT_TO_MASS_HASH_MAP.put("C", 12.0107);
        ELEMENT_TO_MASS_HASH_MAP.put("N", 14.0067);
        ELEMENT_TO_MASS_HASH_MAP.put("O", 15.9994);
        ELEMENT_TO_MASS_HASH_MAP.put("F", 18.99840316);
        ELEMENT_TO_MASS_HASH_MAP.put("Ne", 20.1797);
        ELEMENT_TO_MASS_HASH_MAP.put("Na", 22.98976928);
        ELEMENT_TO_MASS_HASH_MAP.put("Mg", 24.305);
        ELEMENT_TO_MASS_HASH_MAP.put("Al", 26.9815385);
        ELEMENT_TO_MASS_HASH_MAP.put("Si", 28.0855);
        ELEMENT_TO_MASS_HASH_MAP.put("P", 30.973762);
        ELEMENT_TO_MASS_HASH_MAP.put("S", 32.065);
        ELEMENT_TO_MASS_HASH_MAP.put("Cl", 35.453);
        ELEMENT_TO_MASS_HASH_MAP.put("Ar", 39.948);
        ELEMENT_TO_MASS_HASH_MAP.put("K", 39.0983);
        ELEMENT_TO_MASS_HASH_MAP.put("Ca", 40.078);
        ELEMENT_TO_MASS_HASH_MAP.put("Sc", 44.955908);
        ELEMENT_TO_MASS_HASH_MAP.put("Ti", 47.867);
        ELEMENT_TO_MASS_HASH_MAP.put("V", 50.9415);
        ELEMENT_TO_MASS_HASH_MAP.put("Cr", 51.9961);
        ELEMENT_TO_MASS_HASH_MAP.put("Mn", 54.938044);
        ELEMENT_TO_MASS_HASH_MAP.put("Fe", 55.845);
        ELEMENT_TO_MASS_HASH_MAP.put("Co", 58.933194);
        ELEMENT_TO_MASS_HASH_MAP.put("Ni", 58.6934);
        ELEMENT_TO_MASS_HASH_MAP.put("Cu", 63.546);
        ELEMENT_TO_MASS_HASH_MAP.put("Zn", 65.38);
        ELEMENT_TO_MASS_HASH_MAP.put("Ga", 69.723);
        ELEMENT_TO_MASS_HASH_MAP.put("Ge", 72.64);
        ELEMENT_TO_MASS_HASH_MAP.put("As", 74.921595);
        ELEMENT_TO_MASS_HASH_MAP.put("Se", 78.971);
        ELEMENT_TO_MASS_HASH_MAP.put("Br", 79.904);
        ELEMENT_TO_MASS_HASH_MAP.put("Kr", 83.798);
        ELEMENT_TO_MASS_HASH_MAP.put("Rb", 85.4678);
        ELEMENT_TO_MASS_HASH_MAP.put("Sr", 87.62);
        ELEMENT_TO_MASS_HASH_MAP.put("Y", 88.90584);
        ELEMENT_TO_MASS_HASH_MAP.put("Zr", 91.224);
        ELEMENT_TO_MASS_HASH_MAP.put("Nb", 92.90637);
        ELEMENT_TO_MASS_HASH_MAP.put("Mo", 95.95);
        ELEMENT_TO_MASS_HASH_MAP.put("Tc", 98.9072);
        ELEMENT_TO_MASS_HASH_MAP.put("Ru", 101.07);
        ELEMENT_TO_MASS_HASH_MAP.put("Rh", 102.9055);
        ELEMENT_TO_MASS_HASH_MAP.put("Pd", 106.42);
        ELEMENT_TO_MASS_HASH_MAP.put("Ag", 107.8682);
        ELEMENT_TO_MASS_HASH_MAP.put("Cd", 112.414);
        ELEMENT_TO_MASS_HASH_MAP.put("In", 114.818);
        ELEMENT_TO_MASS_HASH_MAP.put("Sn", 118.71);
        ELEMENT_TO_MASS_HASH_MAP.put("Sb", 121.76);
        ELEMENT_TO_MASS_HASH_MAP.put("Te", 127.6);
        ELEMENT_TO_MASS_HASH_MAP.put("I", 126.90447);
        ELEMENT_TO_MASS_HASH_MAP.put("Xe", 131.293);
        ELEMENT_TO_MASS_HASH_MAP.put("Cs", 132.905452);
        ELEMENT_TO_MASS_HASH_MAP.put("Ba", 137.327);
        ELEMENT_TO_MASS_HASH_MAP.put("La", 138.90547);
        ELEMENT_TO_MASS_HASH_MAP.put("Ce", 140.116);
        ELEMENT_TO_MASS_HASH_MAP.put("Pr", 140.90766);
        ELEMENT_TO_MASS_HASH_MAP.put("Nd", 144.242);
        ELEMENT_TO_MASS_HASH_MAP.put("Pm", 144.9);
        ELEMENT_TO_MASS_HASH_MAP.put("Sm", 150.36);
        ELEMENT_TO_MASS_HASH_MAP.put("Eu", 151.964);
        ELEMENT_TO_MASS_HASH_MAP.put("Gd", 157.25);
        ELEMENT_TO_MASS_HASH_MAP.put("Tb", 158.92535);
        ELEMENT_TO_MASS_HASH_MAP.put("Dy", 162.5);
        ELEMENT_TO_MASS_HASH_MAP.put("Ho", 164.93033);
        ELEMENT_TO_MASS_HASH_MAP.put("Er", 167.259);
        ELEMENT_TO_MASS_HASH_MAP.put("Tm", 168.93422);
        ELEMENT_TO_MASS_HASH_MAP.put("Yb", 173.054);
        ELEMENT_TO_MASS_HASH_MAP.put("Lu", 174.9668);
        ELEMENT_TO_MASS_HASH_MAP.put("Hf", 178.49);
        ELEMENT_TO_MASS_HASH_MAP.put("Ta", 180.94788);
        ELEMENT_TO_MASS_HASH_MAP.put("W", 183.84);
        ELEMENT_TO_MASS_HASH_MAP.put("Re", 186.207);
        ELEMENT_TO_MASS_HASH_MAP.put("Os", 190.23);
        ELEMENT_TO_MASS_HASH_MAP.put("Ir", 192.217);
        ELEMENT_TO_MASS_HASH_MAP.put("Pt", 195.084);
        ELEMENT_TO_MASS_HASH_MAP.put("Au", 196.966569);
        ELEMENT_TO_MASS_HASH_MAP.put("Hg", 200.59);
        ELEMENT_TO_MASS_HASH_MAP.put("Tl", 204.3833);
        ELEMENT_TO_MASS_HASH_MAP.put("Pb", 207.2);
        ELEMENT_TO_MASS_HASH_MAP.put("Bi", 208.9804);
        ELEMENT_TO_MASS_HASH_MAP.put("Po", 208.9824);
        ELEMENT_TO_MASS_HASH_MAP.put("At", 209.9871);
        ELEMENT_TO_MASS_HASH_MAP.put("Rn", 222.0176);
        ELEMENT_TO_MASS_HASH_MAP.put("Fr", 223.0197);
        ELEMENT_TO_MASS_HASH_MAP.put("Ra", 226.0245);
        ELEMENT_TO_MASS_HASH_MAP.put("Ac", 227.0277);
        ELEMENT_TO_MASS_HASH_MAP.put("Th", 232.0377);
        ELEMENT_TO_MASS_HASH_MAP.put("Pa", 231.03588);
        ELEMENT_TO_MASS_HASH_MAP.put("U", 238.02891);
        ELEMENT_TO_MASS_HASH_MAP.put("Np", 237.0482);
        ELEMENT_TO_MASS_HASH_MAP.put("Pu", 239.0642);
        ELEMENT_TO_MASS_HASH_MAP.put("Am", 243.0614);
        ELEMENT_TO_MASS_HASH_MAP.put("Cm", 247.0704);
        ELEMENT_TO_MASS_HASH_MAP.put("Bk", 247.0703);
        ELEMENT_TO_MASS_HASH_MAP.put("Cf", 251.0796);
        ELEMENT_TO_MASS_HASH_MAP.put("Es", 252.083);
        ELEMENT_TO_MASS_HASH_MAP.put("Fm", 257.0591);
        ELEMENT_TO_MASS_HASH_MAP.put("Md", 258.0984);
        ELEMENT_TO_MASS_HASH_MAP.put("No", 259.101);
        ELEMENT_TO_MASS_HASH_MAP.put("Lr", 262.1097);
        ELEMENT_TO_MASS_HASH_MAP.put("Rf", 267.1218);
        ELEMENT_TO_MASS_HASH_MAP.put("Db", 268.1257);
        ELEMENT_TO_MASS_HASH_MAP.put("Sg", 269.1286);
        ELEMENT_TO_MASS_HASH_MAP.put("Bh", 274.1436);
        ELEMENT_TO_MASS_HASH_MAP.put("Hs", 277.1519);
        ELEMENT_TO_MASS_HASH_MAP.put("Mt", 278d);
        ELEMENT_TO_MASS_HASH_MAP.put("Ds",  281d);
        ELEMENT_TO_MASS_HASH_MAP.put("Rg",  282d);
        ELEMENT_TO_MASS_HASH_MAP.put("Cn",  285d);
        ELEMENT_TO_MASS_HASH_MAP.put("Nh",  284d);
        ELEMENT_TO_MASS_HASH_MAP.put("Fl",  289d);
        ELEMENT_TO_MASS_HASH_MAP.put("Mc",  288d);
        ELEMENT_TO_MASS_HASH_MAP.put("Lv",  292d);
        ELEMENT_TO_MASS_HASH_MAP.put("Ts",  294d);
        ELEMENT_TO_MASS_HASH_MAP.put("Og",  295d);
    }

    private void loadCharToSubscriptMap() {
        CHAR_TO_SUBSCRIPT_HASH_MAP.put("0", "₀");
        CHAR_TO_SUBSCRIPT_HASH_MAP.put("1", "₁");
        CHAR_TO_SUBSCRIPT_HASH_MAP.put("2", "₂");
        CHAR_TO_SUBSCRIPT_HASH_MAP.put("3", "₃");
        CHAR_TO_SUBSCRIPT_HASH_MAP.put("4", "₄");
        CHAR_TO_SUBSCRIPT_HASH_MAP.put("5", "₅");
        CHAR_TO_SUBSCRIPT_HASH_MAP.put("6", "₆");
        CHAR_TO_SUBSCRIPT_HASH_MAP.put("7", "₇");
        CHAR_TO_SUBSCRIPT_HASH_MAP.put("8", "₈");
        CHAR_TO_SUBSCRIPT_HASH_MAP.put("9", "₉");
    }

    private void loadSubscriptToCharMap() {
        SUBSCRIPT_TO_CHAR_HASH_MAP.put("₀", "0");
        SUBSCRIPT_TO_CHAR_HASH_MAP.put("₁", "1");
        SUBSCRIPT_TO_CHAR_HASH_MAP.put("₂", "2");
        SUBSCRIPT_TO_CHAR_HASH_MAP.put("₃", "3");
        SUBSCRIPT_TO_CHAR_HASH_MAP.put("₄", "4");
        SUBSCRIPT_TO_CHAR_HASH_MAP.put("₅", "5");
        SUBSCRIPT_TO_CHAR_HASH_MAP.put("₆", "6");
        SUBSCRIPT_TO_CHAR_HASH_MAP.put("₇", "7");
        SUBSCRIPT_TO_CHAR_HASH_MAP.put("₈", "8");
        SUBSCRIPT_TO_CHAR_HASH_MAP.put("₉", "9");
    }

    private void loadCharToSuperscriptMap() {
        CHAR_TO_SUPERSCRIPT_HASH_MAP.put("0", "⁰");
        CHAR_TO_SUPERSCRIPT_HASH_MAP.put("1", "¹");
        CHAR_TO_SUPERSCRIPT_HASH_MAP.put("2", "²");
        CHAR_TO_SUPERSCRIPT_HASH_MAP.put("3", "³");
        CHAR_TO_SUPERSCRIPT_HASH_MAP.put("4", "⁴");
        CHAR_TO_SUPERSCRIPT_HASH_MAP.put("5", "⁵");
        CHAR_TO_SUPERSCRIPT_HASH_MAP.put("6", "⁶");
        CHAR_TO_SUPERSCRIPT_HASH_MAP.put("7", "⁷");
        CHAR_TO_SUPERSCRIPT_HASH_MAP.put("8", "⁸");
        CHAR_TO_SUPERSCRIPT_HASH_MAP.put("9", "⁹");
        CHAR_TO_SUPERSCRIPT_HASH_MAP.put("+", "⁺");
        CHAR_TO_SUPERSCRIPT_HASH_MAP.put("-", "⁻");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        menuCheckBoxHighPrecision = menu.getItem(0);
        menuCheckBoxHighPrecision.setChecked(sharedPreferences
                .getBoolean("checkedHighPrecision", false));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_about) {
            startActivity(new Intent(
                    MainActivity.this, AboutActivity.class));
        } else if (item.getItemId() == R.id.menu_check_box_high_precision) {
            item.setChecked(!item.isChecked());
            sharedPreferencesEditor.putBoolean("checkedHighPrecision",
                    item.isChecked());
            sharedPreferencesEditor.apply();
            updateMass();
        }
        return true;
    }

    @Override
    public void beforeTextChanged(
            CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(
            CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        @SuppressWarnings("ConstantConditions") String input
                = chemicalFormulaTextInputEditText.getText().toString();
        LinearLayout linearLayoutOutput
                = findViewById(R.id.linear_layout_output);
        LinearLayout linearLayoutGuide
                = findViewById(R.id.linear_layout_guide);
        HorizontalScrollView horizontalScrollViewSuggestions
                = findViewById(R.id.horizontal_scroll_view_suggestions);
        if (input.equals("")) {
            linearLayoutOutput.setVisibility(View.GONE);
            linearLayoutGuide.setVisibility(View.VISIBLE);
            horizontalScrollViewSuggestions.setVisibility(View.VISIBLE);
        } else {
            linearLayoutOutput.setVisibility(View.VISIBLE);
            linearLayoutGuide.setVisibility(View.GONE);
            horizontalScrollViewSuggestions.setVisibility(View.GONE);
            String ocf = optimizedChemicalFormula(input);
            chemicalFormulaMaterialTextView.setText(ocf);
            updateMass();
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.material_button_copy_chemical_formula
                || v.getId() == R.id.material_button_copy_relative_mass) {
            String stringToCopy = (v.getId()
                    == (R.id.material_button_copy_chemical_formula) ?
                    chemicalFormulaMaterialTextView
                    : massMaterialTextView).getText().toString();
            ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                    .setPrimaryClip(ClipData.newPlainText(
                            "simple text", stringToCopy));
            Toast.makeText(this, "\""
                            + stringToCopy + "\" " + getString(R.string.copied),
                    Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.quick_input_material_button1
                || v.getId() == R.id.quick_input_material_button1
                || v.getId() == R.id.quick_input_material_button2
                || v.getId() == R.id.quick_input_material_button3
                || v.getId() == R.id.quick_input_material_button4
                || v.getId() == R.id.quick_input_material_button5
                || v.getId() == R.id.quick_input_material_button6
                || v.getId() == R.id.quick_input_material_button7
                || v.getId() == R.id.quick_input_material_button8
                || v.getId() == R.id.quick_input_material_button9) {
            String s = ((MaterialButton) v).getText() + "";
            Editable editable
                    = chemicalFormulaTextInputEditText.getEditableText();
            int index = chemicalFormulaTextInputEditText.getSelectionStart();
            if (index < 0 || index >= editable.length()) {
                editable.append(s);
            } else {
                editable.insert(index, s);
            }
        } else {
            for (Chip chip : chips) {
                if (chip.getId() == v.getId()) {
                    chemicalFormulaTextInputEditText.setText(chip.getText());
                    //noinspection ConstantConditions
                    chemicalFormulaTextInputEditText
                            .setSelection(chemicalFormulaTextInputEditText
                                    .getText().length());
                }
            }
        }
    }
}