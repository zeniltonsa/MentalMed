package med.mental.mentalmed.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.List;

import med.mental.mentalmed.model.Pergunta;

public class Preferencias {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String CHAVE_IDUSUARIO = "identificadorUsuario";
    private String CHAVE_QUESTIONARIO = "questionario";
    private String CHAVE_QUESTSQR20 = "questsqr20";
    private String CHAVE_QUESTANSIEDADE = "questansiedade";
    private String CHAVE_QUESTDEPRESSAO = "questdepressao";
    private String CHAVE_QUESTSINDROMEBURNOUT = "questsindromeburnout";

    private Gson gson;

    public Preferencias(Context contextParametro) {
        String NOME_ARQUIVO = "mentalmed.preferencias";
        int MODE = 0;
        preferences = contextParametro.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor = preferences.edit();
        editor.apply();
    }

    public void salvarDados(String idUsuario, Object questionario, Object questSRQ20, Object questAnsiedade, Object questDepressao, Object questSindromeBurnout) {
        gson = new Gson();

        String questionarioJson = gson.toJson(questionario);
        String questSRQ20Json = gson.toJson(questSRQ20);
        String questAnsiedadeJson = gson.toJson(questAnsiedade);
        String questDepressaoJson = gson.toJson(questDepressao);
        String questSindromeBurnoutJson = gson.toJson(questSindromeBurnout);

        editor.putString(CHAVE_IDUSUARIO, idUsuario);
        editor.putString(CHAVE_QUESTIONARIO, questionarioJson);
        editor.putString(CHAVE_QUESTSQR20, questSRQ20Json);
        editor.putString(CHAVE_QUESTANSIEDADE, questAnsiedadeJson);
        editor.putString(CHAVE_QUESTDEPRESSAO, questDepressaoJson);
        editor.putString(CHAVE_QUESTSINDROMEBURNOUT, questSindromeBurnoutJson);
        editor.commit();
    }

    public void salvarSQR20(List<Pergunta> questSRQ20) {
        gson = new Gson();
        String questSRQ20Json = gson.toJson(questSRQ20);
        editor.putString(CHAVE_QUESTSQR20, questSRQ20Json);
        editor.commit();
    }

    public void salvarAnsiedade(List<Pergunta> questAnsiedade) {
        gson = new Gson();
        String questAnsiedadeJson = gson.toJson(questAnsiedade);
        editor.putString(CHAVE_QUESTANSIEDADE, questAnsiedadeJson);
        editor.commit();
    }

    public void salvarDepressao(List<Pergunta> questDepressao) {
        gson = new Gson();
        String questDepressaoJson = gson.toJson(questDepressao);
        editor.putString(CHAVE_QUESTDEPRESSAO, questDepressaoJson);
        editor.commit();
    }

    public void salvarSindromeBurnout(List<Pergunta> questSindromeBurnout) {
        gson = new Gson();
        String questSindromeBurnoutJson = gson.toJson(questSindromeBurnout);
        editor.putString(CHAVE_QUESTSINDROMEBURNOUT, questSindromeBurnoutJson);
        editor.commit();
    }

    public String getIdUsuario() {
        return preferences.getString(CHAVE_IDUSUARIO, null);
    }

    public String getQuestionario() {
        return preferences.getString(CHAVE_QUESTIONARIO, null);
    }

    public String getQuestSQR20() {
        return preferences.getString(CHAVE_QUESTSQR20, null);
    }

    public String getQuestAnsiedade() {
        return preferences.getString(CHAVE_QUESTANSIEDADE, null);
    }

    public String getQuestDepressao() {
        return preferences.getString(CHAVE_QUESTDEPRESSAO, null);
    }

    public String getQuestSindromeBurnout() {
        return preferences.getString(CHAVE_QUESTSINDROMEBURNOUT, null);
    }
}
