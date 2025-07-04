package org.remote.desktop.service.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.VocabularyDao;
import org.remote.desktop.mapper.VocabularyMapper;
import org.remote.desktop.model.dto.VocabularyAdjustmentDto;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.remote.desktop.util.FluxUtil.laterMerger;

@Service
@RequiredArgsConstructor
public class VocabAdjustmentsService {

    private final VocabularyDao vocabularyDao;
    private final VocabularyMapper vocabularyMapper;

    public Function<List<String>, byte[]> concatMaterializedAdjustments(Long langId) {
        return lines -> vocabularyDao.getAllForLanguageRemoving(langId).stream()
                .map(vocabularyMapper::map)
                .reduce(lines, adjuster, laterMerger())
                .stream().collect(Collectors.joining(System.lineSeparator()))
                .getBytes(StandardCharsets.UTF_8);
    }

    BiFunction<List<String>, VocabularyAdjustmentDto, List<String>> adjuster = (acc, dto) ->
            (dto.getFrequencyAdjustment() > 0 ? adjustPositive(dto) : adjustNegative(dto)).apply(acc);

    Function<List<String>, List<String>> adjustNegative(VocabularyAdjustmentDto dto) {
        AtomicInteger i = new AtomicInteger(Math.abs(dto.getFrequencyAdjustment()));

        return lines -> lines.stream()
                .filter(q -> !q.equals(dto.getWord()) || i.decrementAndGet() < 0)
                .toList();
    }

    Function<List<String>, List<String>> adjustPositive(VocabularyAdjustmentDto dto) {
        return lines -> Stream.concat(lines.stream(),
                        IntStream.range(0, dto.getFrequencyAdjustment()).mapToObj(_ -> dto.getWord()))
                .collect(Collectors.toList());
    }

    public static Function<byte[], byte[]> concatTextDocs(byte[] first) {
        return second -> {
            String str1 = new String(first, StandardCharsets.UTF_8);
            String str2 = new String(second, StandardCharsets.UTF_8);

            // Ensure proper line break between blocks
            String merged = str1.endsWith("\n") || str1.isEmpty()
                    ? str1 + str2
                    : str1 + "\n" + str2;

            return merged.getBytes(StandardCharsets.UTF_8);
        };
    }
}
