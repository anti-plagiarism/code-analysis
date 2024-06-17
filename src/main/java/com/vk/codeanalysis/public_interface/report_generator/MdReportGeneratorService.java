package com.vk.codeanalysis.public_interface.report_generator;

import com.vk.codeanalysis.public_interface.dto.report.ReportDto;
import org.springframework.web.multipart.MultipartFile;

public interface MdReportGeneratorService {
    /**
     * Метод конвертации отчета в формат Markdown
     * @param report - обработанные решения
     * @return md отчет
     */
    String convertToMarkdown(ReportDto report);

    /**
     * Метод формирования отчёта для отдельного решения в формате Markdown.
     * В отчет добавляется информация о решении, а также схожие решения, включая их исходный код
     *
     * @param report обработанное решение
     * @return md отчёт
     */
    String convertToMarkdownPrivate(ReportDto report, MultipartFile file);
}
