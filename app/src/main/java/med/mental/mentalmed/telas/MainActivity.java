package med.mental.mentalmed.telas;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import med.mental.mentalmed.R;
import med.mental.mentalmed.config.ConfiguracaoFirebase;
import med.mental.mentalmed.config.Preferencias;
import med.mental.mentalmed.model.Pergunta;
import med.mental.mentalmed.model.PerguntaAnsiedade;
import med.mental.mentalmed.model.PerguntaBurnout;
import med.mental.mentalmed.model.PerguntaDepressaoCat;
import med.mental.mentalmed.model.Questionario;
import med.mental.mentalmed.util.Util;

public class MainActivity extends AppCompatActivity {

    private SpotsDialog progressDialog;

    private final DatabaseReference refConenexao = ConfiguracaoFirebase.getFirebase().child(".info/connected");

    private final DatabaseReference refRespQuestionario = ConfiguracaoFirebase.getFirebase().child("questionario");
    private final DatabaseReference refRespQuestSQR20 = ConfiguracaoFirebase.getFirebase().child("questionarioSQ20");
    private final DatabaseReference refRespQuestAnsiedade = ConfiguracaoFirebase.getFirebase().child("questionarioAnsiedade");
    private final DatabaseReference refRespQuestDepressao = ConfiguracaoFirebase.getFirebase().child("questionarioDepressao");
    private final DatabaseReference refRespQuestSindromeBurnout = ConfiguracaoFirebase.getFirebase().child("questionarioSindromeBurnout");

    private final DatabaseReference refListQuestSQR20 = ConfiguracaoFirebase.getFirebase().child("perguntasSQR20");
    private final DatabaseReference refListQuestAnsiedade = ConfiguracaoFirebase.getFirebase().child("perguntasAnsiedade");
    private final DatabaseReference refListQuestDepressao = ConfiguracaoFirebase.getFirebase().child("perguntasDepressao");
    private final DatabaseReference refListQuestSindromeBurnout = ConfiguracaoFirebase.getFirebase().child("perguntasSindromeBurnout");

    private Questionario questionario;
    private final List<Pergunta> questSRQ20 = new ArrayList<>();
    private final List<PerguntaAnsiedade> questAnsiedade = new ArrayList<>();
    private final List<PerguntaDepressaoCat> questDepressao = new ArrayList<>();
    private final List<PerguntaBurnout> questSindromeBurnout = new ArrayList<>();

    private String idUsuario = "";
    private String androidId = "";

    private ValueEventListener valueEventListenerListaQuestSQR20;
    private ValueEventListener valueEventListenerListaQuestAnsiedade;
    private ValueEventListener valueEventListenerListaQuestDepressao;
    private ValueEventListener valueEventListenerListaQuestSindromeBurnout;

    @Override
    protected void onStart() {
        super.onStart();
        refListQuestSQR20.addValueEventListener(valueEventListenerListaQuestSQR20);
        refListQuestAnsiedade.addValueEventListener(valueEventListenerListaQuestAnsiedade);
        refListQuestDepressao.addValueEventListener(valueEventListenerListaQuestDepressao);
        refListQuestSindromeBurnout.addValueEventListener(valueEventListenerListaQuestSindromeBurnout);
    }

    @Override
    protected void onStop() {
        super.onStop();
        refListQuestSQR20.removeEventListener(valueEventListenerListaQuestSQR20);
        refListQuestAnsiedade.removeEventListener(valueEventListenerListaQuestAnsiedade);
        refListQuestDepressao.removeEventListener(valueEventListenerListaQuestDepressao);
        refListQuestSindromeBurnout.removeEventListener(valueEventListenerListaQuestSindromeBurnout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        carregarComponentes();
        carregarPreferencias();
    }

    public void abrirMainCadastro(View view) {
        refConenexao.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean conectado = dataSnapshot.getValue(Boolean.class);

                if (conectado != null && conectado) {
                    carregarPreferencias();
                    criarPreferencias();

                    Intent intent = new Intent(MainActivity.this, CadastroInicio.class);
                    startActivity(intent);

                } else Util.msg(getApplicationContext(), "Sem conexão!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void abrirLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void criarPreferencias() {
        try {
            if (questionario == null) {
                questionario = new Questionario();
                questionario.setId(androidId);
                questionario.setRespondido(false);

                //SALVA O QUESTIONÁRIO SEM RESPOSTA COMPLETO NO FIREBASE PARA O USUÁRIO
                refRespQuestionario.child(androidId).setValue(questionario)
                        .addOnSuccessListener(aVoid -> Log.i("#SALVAR QUESTIONARIO", "OK"))
                        .addOnFailureListener(e -> {
                            Util.msg(this, "Erro ao salvar registros no Servidor! ERRO: " + e.getLocalizedMessage());
                            Log.i("#SALVAR QUESTIONARIO", "ERRO");
                        });

                //SALVA O QUESTSQR20 SEM RESPOSTA COMPLETO NO FIREBASE PARA O USUÁRIO
                //Salvar no Firebase
                for (Pergunta pergunta : questSRQ20)
                    refRespQuestSQR20.child(androidId).child(String.valueOf(pergunta.getId()))
                            .setValue(pergunta).addOnSuccessListener(aVoid -> Log.i("#SALVAR QUESTSQR20", "OK"));

                //SALVA O QUESTANSIEDADE SEM RESPOSTA COMPLETO NO FIREBASE PARA O USUÁRIO
                //Salvar no Firebase
                for (PerguntaAnsiedade perguntaAnsiedade : questAnsiedade)
                    refRespQuestAnsiedade.child(androidId).child(String.valueOf(perguntaAnsiedade.getId()))
                            .setValue(perguntaAnsiedade).addOnSuccessListener(aVoid -> Log.i("#SALVAR QUESTANSIEDADE", "OK"));

                //SALVA O QUESTDEPRESSAO SEM RESPOSTA COMPLETO NO FIREBASE PARA O USUÁRIO
                //Salvar no Firebase
                for (PerguntaDepressaoCat perguntaDepressaoCat : questDepressao)
                    refRespQuestDepressao.child(androidId).child(String.valueOf(perguntaDepressaoCat.getId()))
                            .setValue(perguntaDepressaoCat).addOnSuccessListener(aVoid -> Log.i("#SALVAR QUESTDEPRESSAOCAT", "OK"));

                //SALVA O QUESTSINDROMEBURNOUT SEM RESPOSTA COMPLETO NO FIREBASE PARA O USUÁRIO
                //Salvar no Firebase
                for (PerguntaBurnout perguntaBurnout : questSindromeBurnout)
                    refRespQuestSindromeBurnout.child(androidId).child(String.valueOf(perguntaBurnout.getId()))
                            .setValue(perguntaBurnout).addOnSuccessListener(aVoid -> Log.i("#SALVAR QUESTSINDROMEBURNOUT", "OK"));

                //Salvar nas Preferências
                Preferencias preferencias = new Preferencias(MainActivity.this);
                preferencias.salvarDados(androidId, questionario, questSRQ20, questAnsiedade, questDepressao, questSindromeBurnout);
            }
        } catch (Exception e) {
            Util.msg(this, "Erro: " + e.getLocalizedMessage() + ". Consulte o suporte!");
            e.printStackTrace();
        }

        if (progressDialog.isShowing()) progressDialog.dismiss();
    }

    private void carregarPreferencias() {
        Preferencias preferencias = new Preferencias(MainActivity.this);
        if (preferencias.getIdUsuario() != null) idUsuario = preferencias.getIdUsuario();

        refRespQuestionario.orderByChild("id").equalTo(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dados : dataSnapshot.getChildren())
                    if (!idUsuario.equals(""))
                        questionario = dados.getValue(Questionario.class);

                Log.i("#CARREGAR QUESTIONARIO", questionario != null ? "OK" : "ERRO");
                if (progressDialog.isShowing()) progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void carregarComponentes() {
        progressDialog = new SpotsDialog(this, "Carregando...", R.style.dialogEmpregosAL);
        progressDialog.setCancelable(false);
        progressDialog.show();

        TextView tv_administrador = findViewById(R.id.tv_administrador);
        tv_administrador.setOnClickListener(v -> abrirLogin());

        carregarListaQuestSQ20();
        carregarListaQuestAnsiedade();
        carregarListaQuestDepressao();
        carregarListaQuestSindromeBurnout();

        if (progressDialog.isShowing()) progressDialog.dismiss();

        //        ArrayList<PerguntaDepressaoCat> listaDePerguntas = new ArrayList<>();
        //        listaDePerguntas.addAll(new Perguntas(this).todasCategoriasPergDepress());
        //
        //        for (int i = 0; i < listaDePerguntas.size(); i++) {
        //            PerguntaDepressaoCat pdc = listaDePerguntas.get(i);
        //
        //            pdc.setPerguntasDeDepressao(new Perguntas(this).perguntaDepressaoPorCat(pdc.getId()));
        //            listaDePerguntas.set(i, pdc);
        //
        //            refListQuestDepressao.child(String.valueOf(pdc.getId())).setValue(pdc);
        //        }
    }

    private void carregarListaQuestSindromeBurnout() {
        valueEventListenerListaQuestSindromeBurnout = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questSindromeBurnout.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    PerguntaBurnout perguntaBurnout = dados.getValue(PerguntaBurnout.class);
                    questSindromeBurnout.add(perguntaBurnout);
                }

                Log.i("#CARREGAR QUESTSINDROMEBURNOUT", questSindromeBurnout.size() > 0 ? "OK" : "ERRO");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void carregarListaQuestDepressao() {
        valueEventListenerListaQuestDepressao = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questDepressao.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    PerguntaDepressaoCat perguntaDepressao = dados.getValue(PerguntaDepressaoCat.class);
                    questDepressao.add(perguntaDepressao);
                }

                Log.i("#CARREGAR QUESTDEPRESSAOCAT", questDepressao.size() > 0 ? "OK" : "ERRO");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void carregarListaQuestAnsiedade() {
        valueEventListenerListaQuestAnsiedade = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questAnsiedade.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    PerguntaAnsiedade perguntaAnsiedade = dados.getValue(PerguntaAnsiedade.class);
                    questAnsiedade.add(perguntaAnsiedade);
                }

                Log.i("#CARREGAR QUESTANSIEDADE", questAnsiedade.size() > 0 ? "OK" : "ERRO");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void carregarListaQuestSQ20() {
        valueEventListenerListaQuestSQR20 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questSRQ20.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    Pergunta pergunta = dados.getValue(Pergunta.class);
                    questSRQ20.add(pergunta);
                }

                Log.i("#CARREGAR QUESTSQR20", questSRQ20.size() > 0 ? "OK" : "ERRO");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
}
