# analyzeMail

Baixa todos anexos de emails que são PDF e XML. Se forem PDF, lê cada um e separa os boletos e notas fiscais por mês.

## Google Gmail

Para conectar no Gmail é necessário ir na console do Google Cloud e fazer os seguintes passos:

1. Habilitar a API do Gmail
2. Criar uma tela de permissão com o escopo `https://mail.google.com/`
3. Criar uma credencial Oauth2
4. Criar uma etiqueta no gmail com o nome de `analyzedMail` para filtrar os emails já analisados
