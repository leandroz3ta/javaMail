package br.com.z3tasistemas.modelo;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class JavaReadMail {

    public static void main(String[] args) {
    	
    	//Configura��o b�sica da conta
    	String servidor = "imap.tropicalpneus.com.br";
    	String email = "suporte@tropicalpneus.com.br";
    	String senha = "nova@202220";
    	
    	
        Properties props = new Properties();
        //Conectamos na conta via IMAP
        props.setProperty("mail.store.protocol", "imaps");
        try {
        	//Aqui inciaremos a conex�o com o servidor de emails
            Session session = Session.getInstance(props, null);
            Store store = session.getStore();
            store.connect(servidor, email, senha);
            
            //Vamos agora abrir a caixa de entrada
            Folder inbox = store.getFolder("INBOX");
            
            //Ela ser� aberta em modo somente leitura
            inbox.open(Folder.READ_ONLY);
            
            //Uma vez dentro da caixa de entrada, pegaremos todas as mensagens
            Message msg = inbox.getMessage(inbox.getMessageCount());
            
            Address[] in = msg.getFrom();
            
            for (Address address : in) {
            	//Exibindo remetente da mensagem
                System.out.println("FROM:" + address.toString());
            }
            
            
            //Daqui em diante � onde a dor de cabe�a come�a...
            
            /*O m�todo multipart � respons�vel por pegar o conte�do da vari�vel
             * msg e analisar sua estrutura*/
            Multipart mp = (Multipart) msg.getContent();
            
            /*
             * O m�todo bodypart pega o corpo da mensagem que est� na vari�vel mp*/
            BodyPart bp = mp.getBodyPart(0);
            
            /*
             * O m�todo MimeMultipart pega o conte�do da var�avel msg
             * e analisa se ela cont�m algum tipo de arquivo
             * */
            MimeMultipart mmp = (MimeMultipart) msg.getContent();
            MimeBodyPart mbp = (MimeBodyPart) mmp.getBodyPart(0);
            

            Multipart multipart = (Multipart) msg.getContent();

            /*O la�o for ir� analisar o conteudo da vari�vel multipart*/
            for (int j = 0; j < multipart.getCount(); j++) {

                BodyPart bodyPart = multipart.getBodyPart(j);

                String disposition = bodyPart.getDisposition();
                	
                	/*Caso seja encontrado algum anexo ele exibir� informa��o do arquivo*/
                  if (disposition != null && (disposition.equalsIgnoreCase("ATTACHMENT"))) { // BodyPart.ATTACHMENT doesn't work for gmail
                      System.out.println("Email possui anexo");

                      DataHandler handler = bodyPart.getDataHandler();
                      System.out.println("Nome do arquivo : " + handler.getName());                                 
                    }
                  else { 
                	  //Caso n�o haja, ele imprime o conte�do da mensagem como texto simples
                      System.out.println("Body: "+bodyPart.getContent());
                      String content= bodyPart.getContent().toString();
                      System.out.println("Conteudo mensagem: "+content);
                    }
            }
            
            /*A problem�tica do neg�cio...
             * Se o email possui anexo, por alguma raz�o que at� ent�o eu desconhe�o,
             * o algoritmo quando configurado como IMAP, n�o retorna a string do 
             * mpp.getContent().toString() mas retorna um objeto do tipo
             * javax.mail.internet.MimeMultipart@
             * j� procurei na internet uma solu��o para o problema mas n�o obtive sucesso.
             * Este problema n�o ocorre quando utilizo POP3, por�m este protocolo
             * n�o trabalha com comandos remotos igual o IMAP faz...
             * Preferencialmente irei utilizar o protocolo IMAP por v�rias raz�es,
             * sendo uma delas o fato de poder trabalhar as mensagens remotamente
             * no servidor sem a necessidade de baix�-las para a maquina local.
             * */
            
            System.out.println("SENT DATE:" + msg.getSentDate());
            System.out.println("SUBJECT:" + msg.getSubject());
            System.out.println("CONTENT:" + bp.getInputStream().toString());   //Aqui deveria imprimir a string mas imprime (com.sun.mail.imap.IMAPInputStream@)	
            System.out.println("Conteudo: "+mbp.getContent().toString()); //Aqui deveria imprimir a string mas imprime (javax.mail.internet.MimeMultipart@)

        } catch (Exception mex) {
            mex.printStackTrace();
        }
    }
}
