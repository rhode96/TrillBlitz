package logic;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import util.Util;

public class Richiesta implements Dao {

	int codice;
	String creatore;
	String luogo; 
	Date data;
	ArrayList<String> listaPartecipanti;

	public Richiesta(int codice, String creatore, String luogo, Date data, ArrayList<String> listaPartecipanti) {
		this.codice = codice;
		this.creatore = creatore;
		this.luogo = luogo;
		this.data = data;
		this.listaPartecipanti = listaPartecipanti;
	}

	public int getCodice() { return codice; };
	public String getCreatore() { return creatore; };
	public String getLuogo() { return luogo; };
	public Date getData() { return data; };
	public ArrayList<String> getListaPartecipanti() { return listaPartecipanti; };

	@Override
	public void save() {
		try {
			String insert = "insert into richiesta(codice,creatore,luogo,data) values (?,?,?,?)";
			PreparedStatement statement = Util.getConnection().prepareStatement(insert);
			statement.setInt(1, codice);
			statement.setString(2, creatore);
			statement.setString(3, luogo);
			statement.setDate(4, data);
			statement.executeUpdate();

			for(String partecipante : listaPartecipanti) {
				insert = "insert into richiede(utente,richiesta) values(?,?)";
				statement = Util.getConnection().prepareStatement(insert);
				statement.setString(1, partecipante);
				statement.setInt(2, codice);
				statement.executeUpdate();
			}



		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}
	@Override
	public void delete() {
		// TODO Auto-generated method stub

	}

	public boolean controlla() {

		boolean ok = false;

		ArrayList<Luogo> listaLuoghi = Luogo.findAll();

		for(Luogo luogo : listaLuoghi) {
			if(luogo.getNome().equals(this.luogo)) {
				System.out.println("ho trovato il luogo");
				ok = true;
			}
		}

		if(!ok) {
			System.out.println("non ho trovato il luogo");
			return false;
		}

		ArrayList<Utente> listaUtenti = Utente.findAll();

		boolean completo = true;
		for(String partecipante : listaPartecipanti) {
			ok = false;
			for(Utente utente : listaUtenti) {
				if(partecipante.equals(utente.getNome())) {
					ok = true;
				}
			}
			if(!ok) {
				System.out.println("non ho trovato l'utente " + partecipante);
				completo = false;
			}
		}
		if(completo) {
			System.out.println("ho trovato tutti i partecipanti");
			return true;
		}
		return false;
	}




	public void accetta() {

		Evento evento = new Evento(codice,luogo,data);   // codice da verificare
		evento.save();

		try {

			String query = "select * from richiede where richiesta = " + codice;
			PreparedStatement statement = Util.getConnection().prepareStatement(query);
			ResultSet result = statement.executeQuery();

			while(result.next()) {

				String utente = result.getString("utente");

				String insert = "insert into partecipa(utente,evento) values(?,?)" ;
				statement = Util.getConnection().prepareStatement(insert);
				statement.setString(1, utente);
				statement.setInt(2, codice);
				statement.executeUpdate();
			}

			String insert = "insert into partecipa(utente,evento) values(?,?)";
			statement = Util.getConnection().prepareStatement(insert);
			statement.setString(1, creatore);
			statement.setInt(2, codice);
			statement.executeUpdate();

			String delete = "delete from richiede where richiesta = " + codice;
			statement = Util.getConnection().prepareStatement(delete);
			statement.executeUpdate();

			delete = "delete from richiesta where codice = " + codice;
			statement = Util.getConnection().prepareStatement(delete);
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} 

	}





	public static ArrayList<Richiesta> findAll(String luogo) {

		ArrayList<Richiesta> listaRichieste = new ArrayList<Richiesta>();

		try {

			String query = "select * from richiesta where luogo = '" + luogo + "'" ;
			PreparedStatement statement = Util.getConnection().prepareStatement(query);
			ResultSet result = statement.executeQuery();

			while(result.next()) {
				int codice = result.getInt("codice");
				String creatore = result.getString("creatore");
				String luogo2 = result.getString("luogo");
				Date data = result.getDate("data");

				ArrayList<String> listaPartecipanti = new ArrayList<String>();

				String query2 = "select * from richiede where richiesta = " + codice;
				PreparedStatement statement2 = Util.getConnection().prepareStatement(query2);
				ResultSet result2 = statement2.executeQuery();

				while(result2.next()) {
					String utente = result2.getString("utente");
					listaPartecipanti.add(utente);
				}

				listaRichieste.add(new Richiesta(codice,creatore,luogo2,data,listaPartecipanti));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} 


		return listaRichieste;
	}
}






