package com.example.webservicesrestful;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webservicesrestful.adapter.ReportAdapter;
import com.example.webservicesrestful.api.ApiService;
import com.example.webservicesrestful.api.RetrofitClient;
import com.example.webservicesrestful.model.Report;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity_REST";

    private ApiService apiService;
    private ReportAdapter reportAdapter;

    private ProgressBar progressBar;
    private EditText etMunicipality, etSymptomSummary, etRiskLevel, etReportId;
    private Button btnCreateReport, btnFetchAll, btnFetchById, btnDeleteReport;
    private TextView tvLogStatus;
    private RecyclerView rvReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        progressBar = findViewById(R.id.progressBar);
        etMunicipality = findViewById(R.id.etMunicipality);
        etSymptomSummary = findViewById(R.id.etSymptomSummary);
        etRiskLevel = findViewById(R.id.etRiskLevel);
        etReportId = findViewById(R.id.etReportId);

        btnCreateReport = findViewById(R.id.btnCreateReport);
        btnFetchAll = findViewById(R.id.btnFetchAll);
        btnFetchById = findViewById(R.id.btnFetchById);
        btnDeleteReport = findViewById(R.id.btnDeleteReport);

        tvLogStatus = findViewById(R.id.tvLogStatus);
        rvReports = findViewById(R.id.rvReports);

        // Setup RecyclerView
        reportAdapter = new ReportAdapter();
        rvReports.setLayoutManager(new LinearLayoutManager(this));
        rvReports.setAdapter(reportAdapter);

        // Initialize API Service
        apiService = RetrofitClient.getApiService();

        // Attach listeners
        btnFetchAll.setOnClickListener(v -> fetchReportsFromServer());

        btnFetchById.setOnClickListener(v -> {
            String idStr = etReportId.getText().toString().trim();
            if (idStr.isEmpty()) {
                Toast.makeText(MainActivity.this, "Ingrese un ID de reporte válido", Toast.LENGTH_SHORT).show();
                return;
            }
            int reportId = Integer.parseInt(idStr);
            fetchReportById(reportId);
        });

        btnCreateReport.setOnClickListener(v -> createNewReport());

        btnDeleteReport.setOnClickListener(v -> {
            String idStr = etReportId.getText().toString().trim();
            if (idStr.isEmpty()) {
                Toast.makeText(MainActivity.this, "Ingrese un ID de reporte para eliminar", Toast.LENGTH_SHORT).show();
                return;
            }
            int reportId = Integer.parseInt(idStr);
            deleteReport(reportId);
        });

        // Automatically trigger initial GET on creation
        fetchReportsFromServer();
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnFetchAll.setEnabled(!loading);
        btnFetchById.setEnabled(!loading);
        btnCreateReport.setEnabled(!loading);
        btnDeleteReport.setEnabled(!loading);
    }

    private void updateLogStatus(String message) {
        tvLogStatus.setText(message);
        Log.d(TAG, message);
    }

    private void fetchReportsFromServer() {
        showLoading(true);
        updateLogStatus("GET /reports en proceso...");

        Call<List<Report>> call = apiService.getAllReports();
        call.enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(@NonNull Call<List<Report>> call, @NonNull Response<List<Report>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Report> reports = response.body();
                    reportAdapter.setReports(reports);
                    String log = "GET Success [HTTP " + response.code() + "]\nSe obtuvieron " + reports.size() + " reportes.";
                    updateLogStatus(log);
                    Toast.makeText(MainActivity.this, "Cargados " + reports.size() + " elementos con éxito.", Toast.LENGTH_SHORT).show();
                } else {
                    String errorLog = "API Error [HTTP " + response.code() + "]: " + response.message();
                    updateLogStatus(errorLog);
                    Toast.makeText(MainActivity.this, "Error de servidor HTTP: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Report>> call, @NonNull Throwable t) {
                showLoading(false);
                String failLog = "NETWORK FAILURE: " + t.getLocalizedMessage();
                updateLogStatus(failLog);
                Toast.makeText(MainActivity.this, "Fallo de red. Verifique su conexión.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchReportById(int reportId) {
        showLoading(true);
        updateLogStatus("GET /reports/" + reportId + " en proceso...");

        Call<Report> call = apiService.getReportById(reportId);
        call.enqueue(new Callback<Report>() {
            @Override
            public void onResponse(@NonNull Call<Report> call, @NonNull Response<Report> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Report report = response.body();
                    reportAdapter.setReports(Collections.singletonList(report));
                    String log = "GET ID Success [HTTP " + response.code() + "]\nReporte #" + report.getId() + ": " + report.getMunicipality();
                    updateLogStatus(log);
                    Toast.makeText(MainActivity.this, "Reporte #" + reportId + " obtenido.", Toast.LENGTH_SHORT).show();
                } else {
                    String errorLog = "API Error [HTTP " + response.code() + "]: No se encontró el reporte #" + reportId;
                    updateLogStatus(errorLog);
                    Toast.makeText(MainActivity.this, "Error HTTP " + response.code() + ": Reporte no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Report> call, @NonNull Throwable t) {
                showLoading(false);
                String failLog = "NETWORK FAILURE: " + t.getLocalizedMessage();
                updateLogStatus(failLog);
                Toast.makeText(MainActivity.this, "Fallo de red al buscar el reporte.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createNewReport() {
        String municipality = etMunicipality.getText().toString().trim();
        String symptomSummary = etSymptomSummary.getText().toString().trim();
        String riskLevel = etRiskLevel.getText().toString().trim();

        if (municipality.isEmpty() || symptomSummary.isEmpty() || riskLevel.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos para el reporte", Toast.LENGTH_SHORT).show();
            return;
        }

        Report newReport = new Report(municipality, symptomSummary, riskLevel);

        showLoading(true);
        updateLogStatus("POST /reports en proceso...");

        Call<Report> call = apiService.createReport(newReport);
        call.enqueue(new Callback<Report>() {
            @Override
            public void onResponse(@NonNull Call<Report> call, @NonNull Response<Report> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Report createdReport = response.body();
                    int createdId = createdReport != null ? createdReport.getId() : -1;
                    if (createdReport != null && createdReport.getMunicipality() == null) {
                        createdReport.setMunicipality(municipality);
                        createdReport.setSymptomSummary(symptomSummary);
                        createdReport.setRiskLevel(riskLevel);
                    }
                    reportAdapter.addReport(createdReport != null ? createdReport : newReport);
                    String log = "POST Success [HTTP " + response.code() + "]\nCreado Reporte ID: " + createdId;
                    updateLogStatus(log);
                    Toast.makeText(MainActivity.this, "Reporte creado exitosamente (ID: " + createdId + ")", Toast.LENGTH_SHORT).show();

                    // Clear fields
                    etMunicipality.setText("");
                    etSymptomSummary.setText("");
                    etRiskLevel.setText("");
                } else {
                    String errorLog = "API Error [HTTP " + response.code() + "]: Fallo al crear reporte";
                    updateLogStatus(errorLog);
                    Toast.makeText(MainActivity.this, "Error de servidor HTTP: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Report> call, @NonNull Throwable t) {
                showLoading(false);
                String failLog = "NETWORK FAILURE: " + t.getLocalizedMessage();
                updateLogStatus(failLog);
                Toast.makeText(MainActivity.this, "Error de red al crear el reporte.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteReport(int reportId) {
        showLoading(true);
        updateLogStatus("DELETE /reports/" + reportId + " en proceso...");

        Call<Void> call = apiService.deleteReport(reportId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    String log = "DELETE Success [HTTP " + response.code() + "]\nReporte #" + reportId + " eliminado.";
                    updateLogStatus(log);
                    Toast.makeText(MainActivity.this, "Reporte #" + reportId + " eliminado exitosamente.", Toast.LENGTH_SHORT).show();
                } else {
                    String errorLog = "API Error [HTTP " + response.code() + "]: No se pudo eliminar reporte #" + reportId;
                    updateLogStatus(errorLog);
                    Toast.makeText(MainActivity.this, "Error HTTP " + response.code() + " al eliminar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                showLoading(false);
                String failLog = "NETWORK FAILURE: " + t.getLocalizedMessage();
                updateLogStatus(failLog);
                Toast.makeText(MainActivity.this, "Error de red al eliminar reporte.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
