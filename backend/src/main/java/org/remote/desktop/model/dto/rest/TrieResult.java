package org.remote.desktop.model.dto.rest;

import com.arun.trie.base.ValueFrequency;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class TrieResult {
    List<ValueFrequency<String>> trie;
    String encoded;
}
