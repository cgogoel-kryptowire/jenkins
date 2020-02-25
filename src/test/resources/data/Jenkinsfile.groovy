node {
    stage 'kryptowire'
    kwSubmit filePath: "${env.KW_BINARY_PATH}", platform: "${env.KW_PLATFORM}"
}
