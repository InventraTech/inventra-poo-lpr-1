//package org.inventra;
//
//import org.inventra.dao.FuncionarioDAO;
//import org.inventra.dao.LoteDAO;
//import org.inventra.dao.ProdutoDAO;
//import org.inventra.dao.RequisicaoDAO;
//import org.inventra.model.FuncionarioMolde;
//import org.inventra.model.LoteMolde;
//import org.inventra.model.ProdutoMolde;
//import org.inventra.model.RequisicaoMolde;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Scanner;
//
//import static java.lang.System.in;
//
//public class Main{
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(in);
//        FuncionarioDAO funcDAO = new FuncionarioDAO();
//        ProdutoDAO produtoDAO = new ProdutoDAO();
//        RequisicaoDAO reqDAO = new RequisicaoDAO();
//        LoteDAO loteDAO = new LoteDAO();
//
//        System.out.print("""
//                Selecione a tabela para realizar a conexão:
//                1. Funcionário;
//                2. Produto;
//                3. Requisição;
//                4. Lote;
//                """);
//
//        int escolhaTabela = sc.nextInt();
//
//        if (escolhaTabela == 1){
//
//            System.out.print("""
//                Selecione a ação para realizar:
//                1. Inserir novo funcionário;
//                2. Consultar lista de funcionários;
//                3. Atualizar coluna;
//                4. Deletar funcionário.
//                """);
//
//            int escolhaAcao = sc.nextInt();
//
//            if (escolhaAcao == 1){
//                sc.nextLine();
//                System.out.print("NOME: ");
//                String nome = sc.nextLine();
//
//                System.out.print("SENHA: ");
//                String senha = sc.nextLine();
//
//                int fk_setor = 1;
//                System.out.print("EMAIL: ");
//                String email = sc.nextLine();
//
//                System.out.print("TELEFONE: ");
//                String telefone = sc.nextLine();
//
//                System.out.print("CPF: ");
//                String cpf = sc.nextLine();
//
//                System.out.print("Digite a data (AAAA-MM-DD): ");
//                String entrada = sc.next(); // Lê a data como texto
//                LocalDate dt_admissao = LocalDate.parse(entrada); // Converte o texto para LocalDate
//
//                String status = "Ativo";
//
//                FuncionarioMolde func = new FuncionarioMolde(nome, senha, email, telefone, cpf, dt_admissao, status, fk_setor);
//                funcDAO.insertFuncionario(func);
//            }
//
//            if(escolhaAcao == 2){
//                List<FuncionarioMolde> TodosFuncionarios = funcDAO.selectFuncionario();
//
//                if (TodosFuncionarios.isEmpty()){
//                    System.out.println("A lista de funcionários está vazia.");
//                } else{
//                    System.out.println("ID | NOME                 | SENHA      | EMAIL           | TELEFONE       | CPF             | ADMISSAO     | STATUS  | SETOR");
//                    for(FuncionarioMolde funcionario : TodosFuncionarios){
//                        System.out.printf("%-2d | %-20s | %-10s | %-15s | %-14s | %-15s | %-12s | %-7s | %-5d%n",
//                                funcionario.getId_funcionario(),
//                                funcionario.getNome(),
//                                funcionario.getSenha(),
//                                funcionario.getEmail(),
//                                funcionario.getTelefone(),
//                                funcionario.getCpf(),
//                                funcionario.getDt_admissao(),
//                                funcionario.getStatus(),
//                                funcionario.getFk_setor()
//                        );
//                        System.out.println("=================================================================================================================================");
//                    }
//                }
//            }
//
//            if (escolhaAcao == 3){
//                String nomeColuna ="";
//                String novoValor = "";
//
//                System.out.print("Digite o ID do funcionário: ");
//                int id_funcionario = sc.nextInt();
//
//                System.out.print("""
//                        Selecione a coluna que deseja atualizar:
//                        1. Nome;
//                        2. Senha;
//                        3. FK_setor;
//                        4. Email;
//                        5. Telefone;
//                        6. CPF;
//                        7. dt_admissao;
//                        """);
//                int numColuna = sc.nextInt();
//                sc.nextLine();
//
//                if (numColuna == 1){
//                    nomeColuna = "nome";
//                    System.out.print("NOVO NOME: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 2){
//                    nomeColuna = "Senha";
//                    System.out.print("NOVA SENHA: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 3){
//                    nomeColuna = "FK_Setor";
//                    System.out.print("NOVA FK_SETOR: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 4){
//                    nomeColuna = "Email";
//                    System.out.print("NOVO EMAIL: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 5){
//                    nomeColuna = "Telefone";
//                    System.out.print("NOVO TELEFONE: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 6){
//                    nomeColuna = "CPF";
//                    System.out.print("NOVO CPF: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 7){
//                    nomeColuna = "dt_admissao";
//                    System.out.println("NOVA DATA ADMISSAO: ");
//                    novoValor = sc.nextLine();
//                }
//
//                funcDAO.updateFuncionario(nomeColuna, novoValor, id_funcionario);
//
//            }
//
//            else if (escolhaAcao == 4){
//                sc.nextLine();
//                System.out.println("Digite o id do funcionário que será apagado: ");
//                int id_funcionario = sc.nextInt();
//
//                System.out.println("Deletando funcionário");
//                funcDAO.deleteFuncionario(id_funcionario);
//            }
//        }
//
//        if (escolhaTabela == 2){
//
//            System.out.print("""
//                Selecione a ação para realizar:
//                1. Inserir novo produto ao estoque;
//                2. Consultar lista de produtos;
//                3. Atualizar coluna;
//                4. Deletar produto.
//                """);
//
//            int escolhaAcao = sc.nextInt();
//
//            if (escolhaAcao == 1){
//                sc.nextLine();
//                System.out.print("NOME PRODUTO: ");
//                String nome = sc.nextLine();
//
//                System.out.print("FK MARCA: ");
//                int fk_marca = sc.nextInt();
//                sc.nextLine();
//                System.out.print("FK CATEGORIA: ");
//                int fk_categoria = sc.nextInt();
//                sc.nextLine();
//                System.out.print("UNIDADE MEDIDA (L, ML, G, KG, UN): ");
//                String unidadeMedida = sc.nextLine();
//
//                System.out.print("ESTOQUE MIN: ");
//                int estoqueMin = sc.nextInt();
//
//                System.out.print("ESTOQUE MAX: ");
//                int estoqueMax = sc.nextInt();
//
//                System.out.print("FK_FORNECEDOR: ");
//                int fk_fornecedor = sc.nextInt();
//                sc.nextLine();
//                System.out.print("DESCRIÇÃO: ");
//                String descricao = sc.nextLine();
//
//                boolean ativo = true;
//                System.out.println("Cadastrando Produto.");
//                ProdutoMolde NovoProduto = new ProdutoMolde(nome, fk_marca, fk_categoria, unidadeMedida, estoqueMin, estoqueMax, fk_fornecedor, descricao, ativo);
//                produtoDAO.insertProduto(NovoProduto);
//            } else if (escolhaAcao == 2) {
//                List<ProdutoMolde> TodosProdutos = produtoDAO.selectProduto();
//
//                if (TodosProdutos.isEmpty()){
//                    System.out.println("A lista de produtos está vazia.");
//                } else{
//                    System.out.println("ID | NOME                 | MARCA | CAT | UN | EST.MIN | EST.MAX | FORN | DESCRIÇÃO            | ATIVO");
//                    for(ProdutoMolde produto : TodosProdutos){
//                        System.out.printf("%-2d | %-20s | %-5d | %-3d | %-2s | %-7d | %-7d | %-4d | %-20s | %-5b%n",
//                                produto.getIdProduto(),
//                                produto.getNome(),
//                                produto.getFk_marca(),
//                                produto.getFk_categoria(),
//                                produto.getUnidadeMedida(),
//                                produto.getEstoqueMin(),
//                                produto.getEstoqueMax(),
//                                produto.getFk_fornecedor(),
//                                produto.getDescricao(),
//                                produto.isAtivo()
//                        );
//                        System.out.println("=================================================================================================================================");
//                    }
//                }
//            } else if(escolhaAcao == 3){
//                String nomeColuna = "";
//                String novoValor = "";
//
//                System.out.print("Digite o ID do produto: ");
//                int id_produto = sc.nextInt();
//
//                System.out.print("""
//                    Selecione a coluna do produto que deseja atualizar:
//                    1. Nome do Produto;
//                    2. FK Marca;
//                    3. FK Categoria;
//                    4. Unidade de Medida;
//                    5. Estoque Mínimo;
//                    6. Estoque Máximo;
//                    7. FK Fornecedor;
//                    8. Descrição;
//                    """);
//                int numColuna = sc.nextInt();
//                sc.nextLine();
//
//                if (numColuna == 1) {
//                    nomeColuna = "nome";
//                    System.out.print("NOVO NOME PRODUTO: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 2) {
//                    nomeColuna = "fk_marca";
//                    System.out.print("NOVA FK MARCA: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 3) {
//                    nomeColuna = "fk_categoria";
//                    System.out.print("NOVA FK CATEGORIA: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 4) {
//                    nomeColuna = "unidade_medida";
//                    System.out.print("NOVA UNIDADE MEDIDA (L, ML, G, KG, UN): ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 5) {
//                    nomeColuna = "estoque_min";
//                    System.out.print("NOVO ESTOQUE MIN: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 6) {
//                    nomeColuna = "estoque_max";
//                    System.out.print("NOVO ESTOQUE MAX: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 7) {
//                    nomeColuna = "fk_fornecedor";
//                    System.out.print("NOVA FK_FORNECEDOR: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 8) {
//                    nomeColuna = "descricao";
//                    System.out.print("NOVA DESCRIÇÃO: ");
//                    novoValor = sc.nextLine();
//                }
//
//                produtoDAO.updateProduto(nomeColuna, novoValor, id_produto);
//            }
//            else if (escolhaAcao == 4) {
//                sc.nextLine();
//                System.out.println("Digite o id do produto que será apagado: ");
//                int id_produto = sc.nextInt();
//
//                System.out.println("Deletando produto...");
//                produtoDAO.deleteProduto(id_produto);
//            }
//
//        }
//
//        if (escolhaTabela == 3){
//
//            System.out.print("""
//                Selecione a ação para realizar:
//                1. Inserir nova requisição;
//                2. Consultar lista de requisições;
//                3. Atualizar coluna;
//                4. Deletar requisição.
//                """);
//
//            int escolhaAcao = sc.nextInt();
//
//            if (escolhaAcao == 1){
//                sc.nextLine();
//                System.out.print("ID TIPO REQUISIÇÃO: ");
//                int id_tipoRequisicao = sc.nextInt();
//
//                System.out.print("QUANTIDADE PRODUTO: ");
//                int quantidade_produto = sc.nextInt();
//                sc.nextLine();
//
//                System.out.print("MOTIVO: ");
//                String motivo = sc.nextLine();
//
//                String status = "Pendente";
//
//                System.out.print("Digite a data e hora (AAAA-MM-DDTHH:MM:SS) Ex: 2026-07-14T23:00:00: ");
//                String entrada = sc.next();
//                LocalDateTime dataHora = LocalDateTime.parse(entrada);
//
//                System.out.print("ID FUNCIONÁRIO CADASTRO: ");
//                int fk_funcionario_cadastro = sc.nextInt();
//
//                System.out.print("ID FUNCIONÁRIO APROVADOR: ");
//                int fk_funcionario_aprovador = sc.nextInt();
//
//                System.out.print("ID PRODUTO: ");
//                int fk_produto = sc.nextInt();
//
//                RequisicaoMolde req = new RequisicaoMolde(id_tipoRequisicao, quantidade_produto, motivo, status, dataHora, fk_funcionario_cadastro, fk_funcionario_aprovador, fk_produto);
//                reqDAO.insertRequisicao(req);
//            }
//
//            if(escolhaAcao == 2){
//                List<RequisicaoMolde> TodasRequisicoes = reqDAO.selectRequisicao();
//
//                if (TodasRequisicoes.isEmpty()){
//                    System.out.println("A lista de requisições está vazia.");
//                } else {
//                    System.out.println("ID | TIPO | QTD | MOTIVO               | STATUS   | DATA E HORA         | CADASTRO | APROVADOR | PRODUTO");
//                    for(RequisicaoMolde requisicao : TodasRequisicoes){
//                        System.out.printf("%-2d | %-4d | %-3d | %-20s | %-8s | %-19s | %-8d | %-9d | %-7d%n",
//                                requisicao.getIdRequisicao(),
//                                requisicao.getIdTipoRequisicao(),
//                                requisicao.getQuantidadeProduto(),
//                                requisicao.getMotivo(),
//                                requisicao.getStatus(),
//                                requisicao.getDataHora().toString(),
//                                requisicao.getFkFuncionarioSolicitante(),
//                                requisicao.getFkFuncionarioAprovador(),
//                                requisicao.getFkProduto()
//                        );
//                        System.out.println("=================================================================================================================================");
//                    }
//                }
//            }
//
//            if (escolhaAcao == 3){
//                String nomeColuna = "";
//                String novoValor = "";
//
//                System.out.print("Digite o ID da requisição: ");
//                int id_requisicao = sc.nextInt();
//
//                System.out.print("""
//                Selecione a coluna que deseja atualizar:
//                1. Tipo Requisição;
//                2. Quantidade Produto;
//                3. Motivo;
//                4. Status;
//                5. DataHora
//                6. Funcionário Solicitante;
//                7. Funcionário Aprovador;
//                8. Produto;
//                """);
//                int numColuna = sc.nextInt();
//                sc.nextLine();
//
//                if (numColuna == 1){
//                    nomeColuna = "id_tipoRequisicao";
//                    System.out.print("NOVO TIPO: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 2){
//                    nomeColuna = "quantidade_produto";
//                    System.out.print("NOVA QUANTIDADE: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 3){
//                    nomeColuna = "motivo";
//                    System.out.print("NOVO MOTIVO: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 4){
//                    nomeColuna = "status";
//                    System.out.print("NOVO STATUS: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 5){
//                    nomeColuna = "datahora";
//                    System.out.print("Digite a nova data e hora (AAAA-MM-DDTHH:MM:SS) Ex: 2026-07-14T23:00:00: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 6){
//                    nomeColuna = "fk_funcionario_solicitante";
//                    System.out.print("NOVO ID CADASTRADOR: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 7){
//                    nomeColuna = "fk_funcionario_aprovador";
//                    System.out.print("NOVO ID APROVADOR: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 8){
//                    nomeColuna = "fk_produto";
//                    System.out.print("NOVO ID PRODUTO: ");
//                    novoValor = sc.nextLine();
//                }
//
//                reqDAO.updateRequisicao(nomeColuna, novoValor, id_requisicao);
//            }
//
//            else if (escolhaAcao == 4){
//                sc.nextLine();
//                System.out.println("Digite o id da requisição que será apagada: ");
//                int id_requisicao = sc.nextInt();
//
//                System.out.println("Deletando requisição...");
//                reqDAO.deleteRequisicao(id_requisicao);
//            }
//        }
//
//        if (escolhaTabela == 4){
//
//            System.out.print("""
//                Selecione a ação para realizar:
//                1. Inserir novo lote;
//                2. Consultar lista de lotes;
//                3. Atualizar coluna;
//                4. Deletar lote.
//                """);
//
//            int escolhaAcao = sc.nextInt();
//
//            if (escolhaAcao == 1){
//                sc.nextLine();
//                System.out.print("ID PRODUTO (FK): ");
//                int fk_produto = sc.nextInt();
//                sc.nextLine();
//
//                System.out.print("NÚMERO DO LOTE: ");
//                String numero_lote = sc.nextLine();
//
//                System.out.print("QUANTIDADE INICIAL: ");
//                int qtd_inicial = sc.nextInt();
//
//                System.out.print("QUANTIDADE ATUAL: ");
//                int qtd_atual = sc.nextInt();
//                sc.nextLine();
//
//                System.out.print("Digite a data de entrada (AAAA-MM-DD): ");
//                String entradaData = sc.nextLine();
//                LocalDate dt_entrada = LocalDate.parse(entradaData);
//
//                System.out.print("Digite a data de validade (AAAA-MM-DD): ");
//                String validadeData = sc.nextLine();
//                LocalDate dt_validade = LocalDate.parse(validadeData);
//
//                System.out.print("VALOR DE CUSTO (Ex: 150.50): ");
//                String valorEntrada = sc.nextLine();
//                // Instanciação recomendada de BigDecimal recebendo String para maior segurança matemática
//                BigDecimal valor_custo = new BigDecimal(valorEntrada);
//
//                System.out.print("NOTA FISCAL: ");
//                String nota_fiscal = sc.nextLine();
//
//                LoteMolde lote = new LoteMolde(fk_produto, numero_lote, qtd_inicial, qtd_atual, dt_entrada, dt_validade, valor_custo, nota_fiscal);
//                loteDAO.insertLote(lote);
//            }
//
//            if(escolhaAcao == 2){
//                List<LoteMolde> TodosLotes = loteDAO.selectLote();
//
//                if (TodosLotes.isEmpty()){
//                    System.out.println("A lista de lotes está vazia.");
//                } else{
//                    System.out.println("ID | PRODUTO | NÚMERO LOTE          | QTD INIC | QTD ATUAL | ENTRADA    | VALIDADE   | VALOR CUSTO | NOTA FISCAL");
//                    for(LoteMolde lote : TodosLotes){
//                        System.out.printf("%-2d | %-7d | %-20s | %-8d | %-9d | %-10s | %-10s | R$ %-8.2f | %-15s%n",
//                                lote.getId_estoque(),
//                                lote.getFk_produto(),
//                                lote.getNumero_lote(),
//                                lote.getQtd_inicial(),
//                                lote.getQtd_atual(),
//                                lote.getDt_entrada(),
//                                lote.getDt_valide(),
//                                lote.getValor_compra(), // O próprio compilador Java formata o BigDecimal com .2f quando solicitado
//                                lote.getNota_fiscal()
//                        );
//                        System.out.println("=================================================================================================================================");
//                    }
//                }
//            }
//
//            if (escolhaAcao == 3){
//                String nomeColuna ="";
//                String novoValor = "";
//
//                System.out.print("Digite o ID do lote: ");
//                int id_estoque = sc.nextInt();
//
//                System.out.print("""
//                Selecione a coluna que deseja atualizar:
//                1. FK Produto;
//                2. Número Lote;
//                3. Quantidade Inicial;
//                4. Quantidade Atual;
//                5. Data Entrada;
//                6. Data Validade;
//                7. Valor Custo;
//                8. Nota Fiscal;
//                """);
//                int numColuna = sc.nextInt();
//                sc.nextLine();
//
//                if (numColuna == 1){
//                    nomeColuna = "fk_produto";
//                    System.out.print("NOVA FK PRODUTO: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 2){
//                    nomeColuna = "numero_lote";
//                    System.out.print("NOVO NÚMERO LOTE: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 3){
//                    nomeColuna = "qtd_inicial";
//                    System.out.print("NOVA QUANTIDADE INICIAL: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 4){
//                    nomeColuna = "qtd_atual";
//                    System.out.print("NOVA QUANTIDADE ATUAL: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 5){
//                    nomeColuna = "dt_entrada";
//                    System.out.print("NOVA DATA ENTRADA (AAAA-MM-DD): ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 6){
//                    nomeColuna = "dt_validade";
//                    System.out.print("NOVA DATA VALIDADE (AAAA-MM-DD): ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 7){
//                    nomeColuna = "valor_compra";
//                    System.out.print("NOVO VALOR DE CUSTO: ");
//                    novoValor = sc.nextLine();
//                }
//                else if (numColuna == 8){
//                    nomeColuna = "nota_fiscal";
//                    System.out.print("NOVA NOTA FISCAL: ");
//                    novoValor = sc.nextLine();
//                }
//
//                loteDAO.updateLote(nomeColuna, novoValor, id_estoque);
//            }
//
//            else if (escolhaAcao == 4){
//                sc.nextLine();
//                System.out.println("Digite o ID do lote que será apagado: ");
//                int id_estoque = sc.nextInt();
//
//                System.out.println("Deletando lote...");
//                loteDAO.deleteLote(id_estoque);
//            }
//        }
//    }
//}