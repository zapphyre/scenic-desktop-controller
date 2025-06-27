package org.remote.desktop.service.impl;

import lombok.RequiredArgsConstructor;
import org.remote.desktop.db.dao.VocabularyDao;
import org.remote.desktop.db.entity.VocabularyAdjustment;
import org.remote.desktop.mapper.VocabularyMapper;
import org.remote.desktop.util.FluxUtil;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabAdjustmentsService {

    private final VocabularyDao vocabularyDao;
    private final VocabularyMapper vocabularyMapper;

//    public byte[] materializeAdjustments(Long langId) {
//        return vocabularyDao.getAllForLanguageRemoving(langId).stream()
//                .map(vocabularyMapper::map)
//                .collect(Collectors.joining(System.lineSeparator()))
//                .getBytes(StandardCharsets.UTF_8);
//    }

    public Function<List<String>, byte[]> concatMaterializedAdjustments(Long langId) {
        return lines -> vocabularyDao.getAllForLanguageRemoving(langId).stream()
                .map(vocabularyMapper::map)
                .reduce(lines, (p, q) -> q.adjust(p), FluxUtil.laterMerger())
                .stream()
                .collect(Collectors.joining(System.lineSeparator()))
                .getBytes(StandardCharsets.UTF_8);
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
