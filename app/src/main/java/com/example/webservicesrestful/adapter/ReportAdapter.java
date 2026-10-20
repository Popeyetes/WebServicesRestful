package com.example.webservicesrestful.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webservicesrestful.R;
import com.example.webservicesrestful.model.Report;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<Report> reports = new ArrayList<>();

    public void setReports(List<Report> reports) {
        this.reports = reports != null ? reports : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addReport(Report report) {
        if (report != null) {
            this.reports.add(0, report);
            notifyItemInserted(0);
        }
    }

    public void clear() {
        this.reports.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reports.get(position);
        holder.tvReportId.setText("ID: " + report.getId());
        holder.tvMunicipality.setText(report.getMunicipality() != null ? report.getMunicipality() : "N/A");
        holder.tvSymptomSummary.setText(report.getSymptomSummary() != null ? report.getSymptomSummary() : "N/A");
        holder.tvRiskLevel.setText("Risk: " + (report.getRiskLevel() != null ? report.getRiskLevel() : "N/A"));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvReportId, tvMunicipality, tvSymptomSummary, tvRiskLevel;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReportId = itemView.findViewById(R.id.tvReportId);
            tvMunicipality = itemView.findViewById(R.id.tvMunicipality);
            tvSymptomSummary = itemView.findViewById(R.id.tvSymptomSummary);
            tvRiskLevel = itemView.findViewById(R.id.tvRiskLevel);
        }
    }
}
