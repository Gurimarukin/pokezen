import * as React from 'react';
import { Link } from 'react-router-dom';

import Names from '../Names';
import Name from '../Name';


interface IPokemonsResponse {
    body(searchString: string): string | JSX.Element[];
}

class Loading implements IPokemonsResponse {
    body(): string {
        return 'Loading...';
    }
}

class GotPokemons implements IPokemonsResponse {
    private names: Names;

    constructor (names: Names) {
        this.names = names;
    }

    body(searchString: string): string | JSX.Element[] {
        if (this.names.isEmpty()) return 'No Pokemons :(';

        const filtered = this.names.filter(searchString.toLowerCase());
        if (filtered.isEmpty()) return 'No matching Pokemons.';

        return filtered.map((name: Name, i: number) => (
            <div key={i}>
                <Link to={`/pokemon/${name}`}>
                    {name.upper().toString()}
                </Link>
            </div>
        ));
    }
}

class PokemonsError implements IPokemonsResponse {
    body(): string {
        return 'Couldn\'t retrieve pokemons.';
    }
}


export interface IPokemonsGetter {
    pokemons: () => Promise<Names>;
}


interface IProps {
    getter: IPokemonsGetter;
}

interface IState {
    names: IPokemonsResponse;
    searchString: string;
}

export default class SearchPokemon extends React.Component<IProps, IState> {
    private inputRef: React.RefObject<HTMLInputElement> = React.createRef();

    state: IState = {
        names: new Loading(),
        searchString: '',
    };

    componentDidMount() {
        this.props.getter.pokemons()
        .then(pokemonNames => this.setState({
            names: new GotPokemons(pokemonNames)
        }))
        .catch(_message => this.setState({
            names: new PokemonsError()
        }));
    }

    render() {
        return (
            <div className='SearchPokemon'>
                <input ref={this.inputRef}
                       onChange={this.onChange}
                       type='text'
                       placeholder='Search for pokemons!' />
                <div>{this.state.names.body(this.state.searchString)}</div>
            </div>
        );
    }

    private onChange = (e: React.FormEvent) => {
        if (this.inputRef.current !== null) {
            this.setState({ searchString: this.inputRef.current.value });
        }
    }
}
