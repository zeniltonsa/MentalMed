package med.mental.mentalmed.telas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import med.mental.mentalmed.R;
import med.mental.mentalmed.config.ConfiguracaoFirebase;
import med.mental.mentalmed.config.Preferencias;
import med.mental.mentalmed.model.Questionario;
import med.mental.mentalmed.util.Util;

public class CadastroFase2 extends AppCompatActivity {

    private ConstraintLayout constraintLayout;

    private EditText et_semetre_grad;
    private EditText et_horas_estudo;
    private EditText et_periodo_atual;

    private RadioGroup radio_group_ativ_acad;
    private RadioGroup radio_group_estuda;

    private Button bt_proximo_1;
    private SpotsDialog progressDialog;

    private final DatabaseReference referenciaQuestionario = ConfiguracaoFirebase.getFirebase().child("questionario");
    private Questionario questionario = new Questionario();
    private String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_fase_2);

        carregarComponentes();
        carregarPreferencias();

        bt_proximo_1.setOnClickListener(v -> abrirAnsiedade());
    }

    private void abrirAnsiedade() {
        try {
            coletarRespostas();

            if (isValid()) {

                if (!questionario.isRespondido()) salvarFirebase();

                Intent intent = new Intent(this, QuestAnsiedade.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            Util.msg(this, "Erro: " + e.getLocalizedMessage() + ". Consulte o suporte!");
            e.printStackTrace();
        }
    }

    private void salvarFirebase() {
        //Salvar no Firebase
        HashMap<String, Object> dadosAtualizar = new HashMap<>();
        dadosAtualizar.put("id", questionario.getId());
        dadosAtualizar.put("idade", questionario.getIdade());
        dadosAtualizar.put("semestreInicioGraduacao", questionario.getSemestreInicioGraduacao());
        dadosAtualizar.put("periodoAtual", questionario.getPeriodoAtual());
        dadosAtualizar.put("rendaFamiliar", questionario.getRendaFamiliar());
        dadosAtualizar.put("horasEstudoDiarios", questionario.getHorasEstudoDiarios());
        dadosAtualizar.put("horasLazerSemanalmente", questionario.getHorasLazerSemanalmente());
        dadosAtualizar.put("genero", questionario.getGenero());
        dadosAtualizar.put("sexo", questionario.getSexo());
        dadosAtualizar.put("moradia", questionario.getMoradia());
        dadosAtualizar.put("raca", questionario.getRaca());
        dadosAtualizar.put("temFilhos", questionario.isTemFilhos());
        dadosAtualizar.put("situacaoConjugal", questionario.isSituacaoConjugal());
        dadosAtualizar.put("estudaETrabalha", questionario.isEstudaETrabalha());
        dadosAtualizar.put("temReligiao", questionario.isTemReligiao());
        dadosAtualizar.put("participaAtividadeAcademica", questionario.isParticipaAtividadeAcademica());
        dadosAtualizar.put("estudaFimDeSemana", questionario.isEstudaFimDeSemana());

        dadosAtualizar.put("respondido", questionario.isRespondido());

        referenciaQuestionario.child(questionario.getId()).updateChildren(dadosAtualizar).addOnSuccessListener(aVoid -> {
            //Salvar nas Preferências
            Preferencias preferencias = new Preferencias(CadastroFase2.this);
            preferencias.salvarQuestionario(questionario);
        });
    }

    private boolean isValid() {
        boolean resultado = true;

        if (questionario.getSemestreInicioGraduacao() == 0) {
            Util.msg(this, "Prencha o semestre que iniciou a graduação");
            resultado = false;
        } else if (questionario.getPeriodoAtual() == 0) {
            Util.msg(this, "Prencha o período atual da sua graduação");
            resultado = false;
        } else if (questionario.getHorasEstudoDiarios() == 0) {
            Util.msg(this, "Informe a quantidade de horas que estuda diariamente");
            resultado = false;
        }

        return resultado;
    }

    private void carregarComponentes() {
        progressDialog = new SpotsDialog(this, "Carregando...", R.style.dialogEmpregosAL);
        progressDialog.setCancelable(false);
        progressDialog.show();

        constraintLayout = findViewById(R.id.constraintLayout);

        bt_proximo_1 = findViewById(R.id.bt_proximo_1);

        et_semetre_grad = findViewById(R.id.et_semetre_grad);
        et_periodo_atual = findViewById(R.id.et_periodo_atual);
        et_horas_estudo = findViewById(R.id.et_horas_estudo);

        radio_group_ativ_acad = findViewById(R.id.radio_group_ativ_acad);
        radio_group_estuda = findViewById(R.id.radio_group_estuda);
    }

    private void carregarPreferencias() {
        Preferencias preferencias = new Preferencias(CadastroFase2.this);
        if (preferencias.getIdUsuario() != null) idUsuario = preferencias.getIdUsuario();

        referenciaQuestionario.orderByChild("id").equalTo(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    questionario = dados.getValue(Questionario.class);
                    Log.i("#CARREGAR QUESTIONARIO FASE2", questionario != null ? "OK" : "ERRO");
                    carregarQuestionario(questionario);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (progressDialog.isShowing()) progressDialog.dismiss();
    }

    private void carregarQuestionario(Questionario questionario) {
        if (questionario.getId() != null
                && questionario.getGenero() != null && questionario.getMoradia() != null
                && questionario.getRaca() != null && questionario.getSexo() != null
                && questionario.getSemestreInicioGraduacao() > 0
                && questionario.getPeriodoAtual() > 0
                && questionario.getHorasEstudoDiarios() > 0) {

            et_semetre_grad.setText(String.valueOf(questionario.getSemestreInicioGraduacao()));
            et_periodo_atual.setText(String.valueOf(questionario.getPeriodoAtual()));
            et_horas_estudo.setText(String.valueOf(questionario.getHorasEstudoDiarios()));

            radio_group_ativ_acad.check(questionario.isParticipaAtividadeAcademica() ? R.id.rb_ativ_acad_sim : R.id.rb_ativ_acad_nao);
            radio_group_estuda.check(questionario.isEstudaFimDeSemana() ? R.id.rb_estuda_sim : R.id.rb_estuda_nao);

            bloquearComponentes();
        }
    }

    private void bloquearComponentes() {
        if (questionario.isRespondido()) {
            for (int i = 0; i < constraintLayout.getChildCount(); i++) {
                View child = constraintLayout.getChildAt(i);
                if (child.getClass().equals(RadioGroup.class) && !child.getClass().equals(AppCompatButton.class)) {
                    RadioGroup radioGroup = (RadioGroup) child;
                    for (int r = 0; r < radioGroup.getChildCount(); r++) {
                        View radio = radioGroup.getChildAt(r);
                        radio.setEnabled(false);
                    }
                }
            }

            et_semetre_grad.setEnabled(false);
            et_periodo_atual.setEnabled(false);
            et_horas_estudo.setEnabled(false);
        }
    }

    private void coletarRespostas() {
        String semestre = et_semetre_grad.getText().toString();
        String periodo = et_periodo_atual.getText().toString();
        String horasEstudo = et_horas_estudo.getText().toString();

        questionario.setSemestreInicioGraduacao(semestre.equals("") ? 0 : Integer.parseInt(semestre));
        questionario.setPeriodoAtual(periodo.equals("") ? 0 : Integer.parseInt(periodo));
        questionario.setHorasEstudoDiarios(horasEstudo.equals("") ? 0 : Float.parseFloat(horasEstudo));

        questionario.setParticipaAtividadeAcademica(radio_group_ativ_acad.getCheckedRadioButtonId() == R.id.rb_ativ_acad_sim);
        questionario.setEstudaFimDeSemana(radio_group_estuda.getCheckedRadioButtonId() == R.id.rb_estuda_sim);
    }
}
