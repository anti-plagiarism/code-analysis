package com.vk.codeanalysis.public_interface.report_generator;

import com.vk.codeanalysis.dto.report.ReportDto;

public interface MdReportGeneratorService {

    /**
     * Метод конвертации отчета в формат Markdown
     * @param report - обработанные решения
     * @return md отчет
     */
    String convertToMarkdown(ReportDto report);
}
